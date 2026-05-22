---
name: "Selecao de Ecossistema Spring Boot"
description: "Convencoes para escolher geracao do Spring Boot, modulos Spring e configuracoes de baseline de producao."
applyTo: "**/*Controller.java,**/*Config.java,**/*Configuration.java,**/application*.yml,**/application*.yaml,**/build.gradle,**/build.gradle.kts,**/pom.xml"
---

# Regras de Selecao do Ecossistema Spring Boot

## Geracao do Boot por baseline Java

- Projetos Java 8/11: usar Spring Boot 2.7.x (trilha de manutencao legada).
- Projetos Java 17/21: usar Spring Boot 3.x por padrao.
- Java 17+ com dependencias validadas e plataforma pronta: avaliar Spring Boot 4.x.

## Requisito de aplicacao das novidades de Java

- Toda proposta de arquitetura deve incluir:
  - principais novidades do baseline Java selecionado
  - como essas novidades serao aplicadas em codigo/runtime
  - quando usar e quando evitar cada novidade

## Politica de dependencias

- Comece com o conjunto minimo de starters necessario.
- Evite adicionar Spring Cloud em monolitos sem requisitos de sistema distribuido.
- Adicione modulos de mensageria/dados/seguranca apenas quando o caso de uso exigir.

## Convencao de build modular (Gradle)

- `domain`: plugin `java-library`, sem dependencias Spring.
- `application`: depende de `domain` e permanece agnostico de framework web.
- `infrastructure`: concentra dependencias Spring e depende de `application` e `domain`.
- Use Java toolchain explicito no root/subprojects para padronizar versao.

## Baseline de producao obrigatorio

- Adicione Actuator para saude e visibilidade operacional.
- Use baseline explicito de seguranca com `SecurityFilterChain`.
- Mantenha configuracao externalizada por profile/ambiente.
- Mantenha a camada de controller enxuta e direcione logica de negocio para casos de uso.

## Padrao de arquivos de configuracao por profile

- Use `application.yml` (ou `application.yaml`) para configuracao base compartilhada.
- Organize overrides em arquivos por profile (`application-development.yml`, `application-local.yml`, `application-production.yml`, `application-test-*.yml`).
- Defina profile ativo por variavel de ambiente (`SPRING_PROFILES_ACTIVE`) e evite profile fixo em codigo.
- Mantenha segredos fora de arquivos versionados (env vars, secret manager ou vault).
- Evite duplicacao de blocos grandes entre profiles; mantenha no base e sobrescreva apenas diferencas.

## Exemplo de baseline de seguranca

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
```

## Compatibilidade com Spring Cloud

- Sempre alinhe o release train do Spring Cloud com sua versao do Spring Boot.
- Importe o BOM do Spring Cloud somente apos confirmar o mapeamento de versoes.
- Nao misture versoes aleatorias de artefatos Cloud fora do controle do BOM.

## Escolhas de dados e integracao

- Padrao relacional transacional: Spring Data JPA.
- Mapeamento relacional simples: Spring Data JDBC.
- Stack relacional reativa: Spring Data R2DBC + WebFlux.
- Integracao com broker: Spring AMQP (RabbitMQ), Spring Kafka ou Spring Cloud Stream.
- Fluxos de integracao corporativa: Spring Integration.

## Requisito de documentacao

- Para cada novo servico/modulo, documente:
  - linha do Boot escolhida e motivo
  - starters/modulos Spring selecionados
  - por que modulos excluidos nao foram usados intencionalmente
- Para definicao completa de seguranca (authn/authz, CORS/CSRF, OAuth2, method security e testes), consulte `../skills/spring-security-application/SKILL.md`.
