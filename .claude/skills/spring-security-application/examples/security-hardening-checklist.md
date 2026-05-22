# Checklist de Hardening - Spring Security

## Arquitetura e fronteiras

- [ ] Regras de negocio em application/domain; controllers sem logica de autorizacao complexa.
- [ ] Existe separacao clara entre rotas publicas, autenticadas e administrativas.
- [ ] Existe regra catch-all (`anyRequest().authenticated()`) no fim da cadeia.

## Autenticacao e autorizacao

- [ ] Modelo de auth definido (JWT, opaque, session/form).
- [ ] Authorities/roles estao mapeadas e documentadas por endpoint.
- [ ] Method security ativo com `@EnableMethodSecurity` quando houver regra fina.
- [ ] SpEL complexo foi reduzido com role hierarchy ou autorizador dedicado.

## Exploit protection

- [ ] CORS configurado para origens/metodos estritamente necessarios.
- [ ] CSRF avaliado por tipo de cliente (browser vs non-browser).
- [ ] Headers de seguranca e politica de logout revisados.

## Actuator e operacao

- [ ] Exposicao de actuator minima (ex: health/info/prometheus).
- [ ] Endpoints sensiveis protegidos por role dedicada (ex: ACTUATOR).
- [ ] Segredos nao estao em arquivo versionado.

## OAuth2 Resource Server

- [ ] `issuer-uri` configurado.
- [ ] `audiences` configurado quando exigido.
- [ ] Timeout/caching do decoder ajustado para ambiente de producao quando necessario.

## Testes

- [ ] Testes de 401/403 para endpoints criticos.
- [ ] Testes de method security com casos permitido/negado.
- [ ] Testes de CSRF para metodos unsafe.
