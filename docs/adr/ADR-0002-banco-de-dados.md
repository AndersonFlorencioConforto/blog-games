# ADR-0002 - Estrategia de Banco de Dados por Profile

**Data:** 2026-05-21  
**Status:** Aceito  
**Decisores:** Anderson Florencio Conforto

---

## Contexto

O projeto precisa de uma estrategia de banco de dados que permita desenvolvimento local agil sem dependencias externas, mas que escale para producao com persistencia confiavel. Os dados sao majoritariamente relacionais: jogos, usuarios, estantes, threads, respostas, likes e denuncias possuem relacionamentos bem definidos.

---

## Decisao

- **Profile `local`:** H2 em modo embedded (in-memory ou file-based para persistencia entre restarts)
- **Profile `dev`, `staging`, `production`:** PostgreSQL 16+

---

## Opcoes Avaliadas

### Opcao A - PostgreSQL em todos os ambientes (via Docker local)
**Pros:** Paridade total entre ambientes; sem surpresas de comportamento  
**Contras:** Requer Docker instalado; overhead para desenvolvimento rapido

### Opcao B - H2 local + PostgreSQL nos demais (ESCOLHIDA)
**Pros:** Zero dependencias para rodar localmente; Hibernate abstrai diferencas; H2 tem modo de compatibilidade PostgreSQL  
**Contras:** Possibilidade de comportamentos divergentes em queries especificas (ex: funcoes de data, full-text search)

### Opcao C - MongoDB
**Pros:** Schema flexivel para dados variados de jogos  
**Contras:** Relacionamentos complexos (estante, follows, threads) sao mais naturais em SQL; joins e transacoes ACID sao necessarios; descartado

### Opcao D - MySQL/MariaDB
**Pros:** Amplamente conhecido  
**Contras:** PostgreSQL tem melhor suporte a tipos avancados (JSONB, full-text), melhor conformidade com SQL padrao e melhor performance em leituras concorrentes

---

## Configuracao do H2 para Profile Local

```properties
# application-local.properties
spring.datasource.url=jdbc:h2:file:./data/blogdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
```

**Premissa P-011:** O modo `MODE=PostgreSQL` do H2 minimiza divergencias. Funcionalidades especificas do PostgreSQL (ex: `tsvector` para full-text) devem ser evitadas no escopo atual ou abstraidas via repositorio.

---

## Configuracao PostgreSQL (dev/staging/production)

```properties
# application-dev.properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

**Migracao de schema:** Flyway sera usado para todos os ambientes exceto `local`. Para `local` pode-se usar `ddl-auto=update` com cautela.

---

## Trade-offs

| Criterio | H2 local | PostgreSQL prod |
|---|---|---|
| Setup inicial | Zero dependencias | Requer servidor ou container |
| Paridade de comportamento | Parcial (modo compat.) | Total |
| Performance em desenvolvimento | Alta (in-memory) | Dependente de rede/disco |
| Tipos avancados (JSONB, arrays) | Limitado | Completo |
| Transacoes ACID | Sim | Sim (mais robusto) |

---

## Consequencias

- Flyway deve ser adicionado como dependencia em `infrastructure`
- Migrations Flyway ficam em `infrastructure/src/main/resources/db/migration/`
- Nao usar tipos ou funcoes especificas do PostgreSQL nas queries JPA para garantir compatibilidade com H2 local
- Connection pool: HikariCP (padrao do Spring Boot) com `maximumPoolSize=10` para dev e configuravel via env vars para producao
- Profile `local` pode usar `ddl-auto=create-drop` para testes de integracao ou `update` para desenvolvimento continuo
