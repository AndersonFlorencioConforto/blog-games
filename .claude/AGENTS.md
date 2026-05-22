# Agentes Disponíveis - Blog Games

Este documento lista todos os agentes AI disponíveis para desenvolvimento do Blog Games.

## 🎯 Backend (Java + Spring Boot)

### planner

**Descrição**: Monta um plano de implementação com impacto arquitetural e mapeamento de riscos antes de codificar.

**Quando usar**: Quando você quer quebrar uma feature/bug complexo em tarefas ordenadas.

**Exemplo**:

```
@planner

Planeje a implementação de um novo domínio "Wishlist" com:
- Relacionamento User ↔ Game (N:M)
- Status (added, removed)
- Endpoints GET/POST/DELETE
```

---

### implementer

**Descrição**: Implementa o escopo aprovado de ponta a ponta com segurança arquitetural e testes direcionados.

**Quando usar**: Quando você tem um plano pronto e quer executar de verdade.

**Exemplo**:

```
@implementer

Implemente o domínio Wishlist baseado no plano:
- Domain: WishlistEntry value object
- Application: AddToWishlistUseCase, RemoveFromWishlistUseCase
- Infrastructure: WishlistController, JPA repository
- Tests: Testes unitários dos casos de uso
```

---

### java-spring-architect

**Descrição**: Desenha stacks Java e Spring Boot com validação de compatibilidade, seleção de módulos e decisões seguras para migração.

**Quando usar**: Quando você quer validar/atualizar versões, adicionar módulos Spring, ou migrar Java.

**Exemplo**:

```
@java-spring-architect

Avalie a viabilidade de:
1. Atualizar de Java 25 para Java 26 (quando lançar)
2. Adicionar Spring Cloud (Config, Gateway) para arquitetura distribuída futura
3. Impacto em dependências e testes
```

---

### project-discovery-architect

**Descrição**: Define arquitetura de um projeto do zero e gera PRD, ADRs e documentos técnicos que guiam implementação.

**Quando usar**: Quando você quer documentar decisões técnicas ou validar um projeto novo.

**Exemplo**:

```
@project-discovery-architect

Crie um ADR (Architecture Decision Record) para:
- Uso de JWT + refresh token rotation
- Justifique segurança, trade-offs, alternativas consideradas
```

---

## 🎨 Frontend (Angular)

### frontend-angular-generator ⭐ **NOVO**

**Descrição**: Especialista em geração de frontend Angular modular e escalável baseado em especificação de APIs e PRD.

**Quando usar**: Quando você quer gerar o frontend Angular completo ou por fases.

**Exemplo - Fase 1 (Bootstrap)**:

```
@frontend-angular-generator

Gere a Fase 1 (Bootstrap) do frontend Angular:
- Angular 18+
- Estrutura core/shared/features
- Roteamento com lazy loading
- AuthService básico
- Package.json com scripts
- Instruções de setup
```

**Exemplo - Fase 2 (Auth)**:

```
@frontend-angular-generator

Gere a Fase 2 (Auth) do frontend Angular:
- LoginPage com validações
- RegisterPage
- JwtInterceptor com refresh automático
- AuthGuard e RoleGuard
- Proteção de rotas por role (USER, ADMIN, MODERATOR, EDITOR)
```

**Exemplo - Fase 3 (Serviços)**:

```
@frontend-angular-generator

Gere a Fase 3 (Serviços) do frontend Angular:
- AuthService completo (refresh token handling)
- UserService (profile, follow, shelf, favorites)
- GameService (list, detail, rate, rating summary)
- ThreadService (list, detail, create)
- ReplyService (create, list)
- NewsService (list, detail)
- Modelos/DTOs tipados
- Error handling global
```

**Exemplo - Fase 4 (Páginas & Componentes)**:

```
@frontend-angular-generator

Gere a Fase 4 (Páginas & Componentes) do frontend Angular com:
- Dashboard/Home page
- Games: List, Detail, Rate
- Users: Profile, Shelf, Followers, Following
- Threads: List, Detail, Create
- News: List, Detail
- Admin: Games, Categories, Reviews, News, Reports
- Componentes compartilhados: Header, Footer, Cards, Pagination
- Validações, loading states, error handling
```

**Exemplo - Completo**:

```
@frontend-angular-generator

Gere o frontend Angular completo (todas as fases):
- Fase 1: Bootstrap
- Fase 2: Auth
- Fase 3: Serviços
- Fase 4: Páginas & Componentes
- CSS Framework: Tailwind CSS
- State Management: RxJS (simples) ou NgRx (se complexidade exigir)
- Build otimizado para produção
- Docker support opcional
- README completo
```

---

## 🛠️ Skills Disponíveis (Funções Reutilizáveis)

Uso: `@skill <nome-da-skill>`

### Backend Skills

- `java-spring-bootstrap`: Scaffold de stack Java + Spring Boot
- `ddd-use-case-scaffold`: Scaffold de fluxo completo (domain/app/infra)
- `java-design-pattern-application`: Aplicação de Design Patterns
- `java-solid-application`: Diagnóstico e refatoração SOLID
- `spring-rest-endpoint`: Implementação de endpoint REST
- `docs-api-usage`: Documentacao de uso de endpoints e funcionalidades (payload, response, erros)
- `spring-security-application`: Aplicação de Spring Security 7
- `spring-transactional-usage`: Definição de fronteiras transacionais
- `architecture-consistency-check`: Auditoria de consistência arquitetural

### Frontend Skills (NOVO)

- `angular-app-bootstrap`: Scaffold Angular com config base, routing, environment
- `angular-services-architecture`: Arquitetura de serviços HTTP tipados
- `angular-pages-components`: Geração de páginas e componentes por feature
- `angular-routing-lazy-loading`: Roteamento avançado com lazy loading e guards

---

## 🎬 Fluxos Recomendados

### Novo Backend Feature

```
1. planner              -> Quebra em tarefas
2. implementer          -> Executa
3. ddd-use-case-scaffold (skill) -> Garante qualidade arquitetural
```

### Novo Frontend Feature

```
1. planner                           -> Quebra em tarefas
2. frontend-angular-generator        -> Gera scaffolds
3. skill: angular-services-architecture -> Detalha serviços
4. skill: angular-pages-components      -> Detalha componentes
```

### Validação Arquitetural

```
1. skill: architecture-consistency-check -> Auditoria
2. skill: java-solid-application         -> Validação SOLID
3. implementer                           -> Correções
```

### Decisão Técnica

```
1. project-discovery-architect -> ADR/documentação
2. java-spring-architect       -> Decisões Java/Spring
```

---

## 📋 Como Invocar Agentes

### No Claude Code

Os agentes são invocados pelo Claude durante a conversa. Mencione o agente pelo nome na mensagem:

```
Use o agente frontend-angular-generator para gerar a Fase 2 (Auth) do frontend Angular com:
- LoginPage com validações
- RegisterPage
- JwtInterceptor
- Guards por role
```

Ou peça ao Claude Code diretamente:

```
@frontend-angular-generator gere a Fase 2 (Auth)...
```

Os arquivos de definição dos agentes estão em `.claude/agents/<nome>.md`.

---

## 📚 Referências

- [Playbook de Customizações](./ai-customizations-playbook.md)
- [Regras Java DDD](./rules/java-ddd.md)
- [Skill Generator Frontend](./skills/generate-angular-frontend/SKILL.md)
- [API Catalog Backend](../docs/architecture/api-catalog.md)
- [Domain Architecture Backend](../docs/architecture/domains-architecture.md)
