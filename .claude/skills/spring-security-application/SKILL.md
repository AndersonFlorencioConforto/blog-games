---
name: spring-security-application
description: Define e aplica Spring Security 7 em Spring Boot 4.x com boas praticas para APIs, OAuth2, method security e operacao segura em producao. Use quando configurar autenticacao ou autorizacao, SecurityFilterChain, filtro JWT, regras de rota publica e protegida, roles, method security com PreAuthorize, ou hardening de seguranca.
argument-hint: "<tipo-api> <modelo-auth> <rotas-publicas> [restricoes]"
---

# Skill de Spring Security (Boot 4 + Java 25)

Use esta skill para desenhar, implementar ou revisar seguranca de aplicacoes Spring com foco em API backend.

## Escopo

- Autenticacao e autorizacao em APIs Servlet (e nota para WebFlux).
- SecurityFilterChain com regras por rota e por modulo.
- OAuth2 Resource Server (JWT ou opaque token).
- Method security com `@EnableMethodSecurity`.
- CORS, CSRF, headers e hardening de endpoint.
- Seguranca de actuator e observabilidade.
- Testes de seguranca com `spring-security-test`.
- Notas de compatibilidade para Java 25 + Spring Boot 4.x + Spring Security 7.x.

## Entradas esperadas

- Tipo da API (publica, interna, BFF, admin).
- Modelo de auth (JWT, opaque token, session/form, mTLS complementar).
- Rotas publicas e protegidas.
- Regras de role/authority por endpoint e por caso de uso.
- Exigencias de compliance (auditoria, expiracao, CORS, headers, etc).

## Procedimento

1. Defina a estrategia de autenticacao e autorizacao (quem autentica e quem autoriza).
2. Separe rotas publicas, autenticadas e administrativas.
3. Configure `SecurityFilterChain` com DSL moderna (`authorizeHttpRequests`, sem `and()`).
4. Ative `@EnableMethodSecurity` para regras finas no servico de aplicacao.
5. Em JWT, configure `issuer-uri` e, quando necessario, `audiences`.
6. Defina mapeamento de authorities (scope -> authority) evitando logica duplicada.
7. Configure CORS antes de problemas de preflight; use `UrlBasedCorsConfigurationSource` quando necessario.
8. Trate CSRF de forma intencional:
   - browser/session: manter habilitado
   - API stateless para non-browser: avaliar desabilitar com justificativa
   - SPA: considerar `csrf.spa()` e refresh de token apos login/logout
9. Proteja actuator com cadeia dedicada ou matcher especifico (`EndpointRequest`).
10. Adicione testes de seguranca para authn/authz/403/401/CSRF.
11. Revise anti-padroes e finalize checklist operacional.

## Verificacoes obrigatorias

- Existe pelo menos um `SecurityFilterChain` explicito quando for customizar regras.
- Nao ha regra aberta acidental (`permitAll`) em endpoints sensiveis.
- Method security usa `@EnableMethodSecurity` (nao `@EnableGlobalMethodSecurity`).
- Resource Server valida `iss` e, quando aplicavel, `aud`.
- CORS configurado para origens/metodos estritamente necessarios.
- CSRF nao foi desabilitado sem justificativa de arquitetura.
- Actuator nao esta exposto de forma ampla sem controle de acesso.
- Testes cobrem caminho feliz e negado (401/403) para rotas criticas.

## Anti-padroes comuns

- Colocar regra de autorizacao de negocio em controller em vez de application/service.
- Usar expressoes SpEL complexas demais sem role hierarchy ou autoridade dedicada.
- Desabilitar CORS para "resolver erro de navegador".
- Usar usuario/senha default em producao.
- Expor `management.endpoints.web.exposure.include=*` sem protecao.
- Misturar varias estrategias de auth sem separar cadeias de filtro.

## Notas de compatibilidade (varredura)

- Spring Security 7 remove APIs antigas e reforca DSL lambda.
- `authorizeRequests` foi substituido por `authorizeHttpRequests`.
- `AntPathRequestMatcher` e `MvcRequestMatcher` foram removidos em favor de `PathPatternRequestMatcher`.
- Em Security 7, ha suporte a MFA e melhorias em AuthorizationManager.
- Spring Boot 4 usa baseline moderno de framework e integra Security 7 por padrao na linha atual.
- Java 25 (LTS) traz recursos novos; use features preview/incubator apenas com governanca explicita.

## Recursos

- [Template SecurityFilterChain JWT](./templates/security-filter-chain-jwt-template.java.template)
- [Template Method Security](./templates/method-security-config-template.java.template)
- [Template de propriedades de seguranca](./templates/application-security-template.yml.template)
- [Template de testes de seguranca MockMvc](./templates/security-mockmvc-test-template.java.template)
- [Checklist de hardening](./examples/security-hardening-checklist.md)
- [Guia Boot 4 + Security 7 + Java 25](./examples/boot4-security7-java25-notes.md)
- [Guia de referencia do projeto](../../java-spring-reference.md)
