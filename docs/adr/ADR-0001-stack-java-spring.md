# ADR-0001 - Stack Java 25 + Spring Boot 4.x

**Data:** 2026-05-21  
**Status:** Aceito  
**Decisores:** Anderson Florencio Conforto

---

## Contexto

O projeto e uma plataforma backend REST API para catalogo de jogos. A stack precisa ser definida antes do inicio da implementacao. O desenvolvedor ja possui familiaridade com Java e Spring Boot. O projeto usa Gradle como build tool com modulos separados para domain, application e infrastructure.

---

## Decisao

Adotar **Java 25** com **Spring Boot 4.x** (versao estavel mais recente disponivel no momento do inicio da implementacao).

---

## Opcoes Avaliadas

### Opcao A - Java 25 + Spring Boot 4.x (ESCOLHIDA)
- Java 25 e a versao mais recente com suporte a Virtual Threads (Project Loom) estavel, Records, Pattern Matching e Sealed Classes maduros
- Spring Boot 4.x traz Spring Security 7, Jakarta EE 11, suporte nativo a Virtual Threads
- Gradle 8.x com suporte a Java 25

### Opcao B - Java 21 LTS + Spring Boot 3.x
- Java 21 e LTS com suporte ate 2028
- Spring Boot 3.x e amplamente adotado com ecossistema consolidado
- Opcao mais conservadora; adequada para times grandes com preocupacao de suporte

### Opcao C - Kotlin + Spring Boot
- Reducao de verbosidade; coroutines nativas
- Curva de aprendizado adicional para o desenvolvedor
- Descartado por nao haver justificativa de produtividade suficiente vs custo de adocao

---

## Trade-offs

| Criterio | Java 25 + Boot 4.x | Java 21 + Boot 3.x |
|---|---|---|
| Suporte LTS | Nao (Java 25 nao e LTS) | Sim (Java 21 LTS ate 2028) |
| Recursos de linguagem | Mais recentes | Maduros e estaveis |
| Ecossistema/libs | Requer compatibilidade verificada | Amplamente compativel |
| Virtual Threads | Estavel e melhorado | Disponivel desde Java 21 |
| Spring Security | Versao 7 (Boot 4.x) | Versao 6 (Boot 3.x) |
| Risco de breaking changes | Maior | Menor |

---

## Consequencias

- O `build.gradle` de cada modulo deve especificar `sourceCompatibility = JavaVersion.VERSION_25`
- Dependencias de terceiros devem ser validadas quanto a compatibilidade com Java 25
- Anotacoes de framework (Spring, JPA) ficam exclusivamente na camada `infrastructure`; camada `domain` usa apenas Java puro
- Virtual Threads devem ser habilitados via `spring.threads.virtual.enabled=true` no `application.properties` da infrastructure

---

## Estrutura de Modulos

```
blog/
  domain/          - Entidades, value objects, agregados, eventos, portas (interfaces)
  application/     - Casos de uso, servicos de aplicacao, DTOs de comando/resposta
  infrastructure/  - Controllers, repositorios JPA, configuracao Spring, seguranca, e-mail
```

**Regra de dependencia:**
- `infrastructure` depende de `application`
- `application` depende de `domain`
- `domain` nao depende de nenhum modulo interno nem de frameworks
