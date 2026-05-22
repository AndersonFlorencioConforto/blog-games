# Notas de Stack: Boot 4.x + Security 7.x + Java 25

Baseado em varredura de documentacao oficial (Spring e OpenJDK).

## O que muda no Spring Security 7

- DSL moderna reforcada: usar lambda DSL, sem `and()`.
- Preferir `authorizeHttpRequests` (legado `authorizeRequests` removido).
- Mudancas de matcher: foco em `PathPatternRequestMatcher`.
- `@EnableMethodSecurity` como padrao para method authorization.
- Avancos em `AuthorizationManager` e suporte a MFA.
- Authorization Server integrado ao ecossistema principal.

## O que observar em Spring Boot 4

- Auto-config de seguranca continua protegendo app por default quando Security esta no classpath.
- Ao declarar `SecurityFilterChain`, auto-config de seguranca web faz backoff e voce controla as regras.
- Actuator requer estrategia explicita de exposicao e autorizacao.

## Java 25 (LTS) e seguranca

- Java 25 e LTS e pode ser usado como baseline apos validacao de dependencias/tooling.
- Features preview/incubator devem ser opt-in com plano de fallback.
- Para seguranca, foco em estabilidade: evitar acoplamento de regras de auth com features preview.

## Boas praticas recomendadas

1. API stateless + OAuth2 Resource Server JWT para servicos backend.
2. `issuer-uri` + `audiences` para validacao de token.
3. Method security para regras de negocio finas e defesa em profundidade.
4. CORS configurado de forma restritiva; nao desabilitar para "sumir erro".
5. CSRF habilitado para browser/session; avaliar desabilitar apenas em API non-browser.
6. Testar sempre 401/403 e cenarios de CSRF.

## Riscos comuns em migracao

- Migrar para Security 7 mantendo APIs removidas do DSL anterior.
- Expor actuator em excesso.
- Remover CSRF sem considerar clientes browser.
- SpEL muito complexo sem hierarquia de roles.
- Ausencia de testes de autorizacao por endpoint e metodo.

## Referencias oficiais

- Spring Security reference: https://docs.spring.io/spring-security/reference/
- Spring Boot security: https://docs.spring.io/spring-boot/reference/web/spring-security.html
- Spring Boot actuator security: https://docs.spring.io/spring-boot/reference/actuator/endpoints.html
- Spring Security what is new 7.0: https://docs.spring.io/spring-security/reference/whats-new.html
- OpenJDK 25: https://openjdk.org/projects/jdk/25/
