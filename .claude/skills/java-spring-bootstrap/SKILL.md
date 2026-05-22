---
name: java-spring-bootstrap
description: Faz bootstrap de uma stack Java + Spring Boot com escolhas seguras de versao, starters minimos e baseline de producao. Use quando iniciar um projeto novo, configurar build.gradle ou pom, escolher versao de Java e Spring Boot, definir profiles e dependencias base, ou montar baseline de seguranca e observabilidade.
argument-hint: "<tipo-de-projeto> <restricoes> [versao-java]"
---

# Skill de Bootstrap Java Spring

Use esta skill para desenhar um baseline reutilizavel e seguro de versao para projetos Java e Spring.

## Entradas esperadas

- Tipo de projeto (API monolito, microservice, batch worker, consumidor de eventos, gateway)
- Restricoes (runtime, compliance, provedor cloud, familiaridade do time)
- Baseline Java opcional (8, 11, 17, 21, 25)

## Procedimento

1. Escolha o baseline Java e mapeie para uma linha compativel de Spring Boot.
2. Selecione apenas starters e modulos Spring necessarios.
3. Gere baseline de build (Gradle ou Maven) com Java toolchain/release explicito.
4. Adicione baseline de seguranca e observabilidade.
5. Defina estrategia de configuracao por ambiente (`application.yml` + `application-{profile}.yml`).
6. Adicione modulos opcionais de cloud/mensageria apenas quando necessario.
7. Forneca notas de migracao se o baseline for Java 8/11.
8. Descreva novidades de versao para aplicar no baseline Java escolhido (o que, como, quando).
9. Liste passos de teste e validacao de runtime.

## Verificacoes obrigatorias

- A versao Java esta explicita no arquivo de build.
- A linha do Boot e compativel com o baseline Java selecionado.
- O baseline de seguranca esta presente para APIs.
- A estrategia de endpoints do Actuator esta definida.
- Existe referencia explicita para `application.yml` e perfis (`development`, `local`, `production`, `test-*`).
- Nao ha dependencia desnecessaria de Spring Cloud por padrao.
- Features preview de Java estao documentadas se habilitadas.
- A saida deve incluir notas praticas de adocao de features para a versao Java selecionada.

## Formato de saida

1. Versoes Java e Spring escolhidas com justificativa.
2. Lista de dependencias/starters selecionados.
3. Snippet de template de build.
4. Snippet de baseline de seguranca + runtime.
5. Notas de aplicacao de features Java (como e quando usar neste contexto).
6. Riscos e plano de migracao.

## Recursos

- [Template Gradle](./templates/build-gradle-template.gradle.template)
- [Template Maven](./templates/pom-template.xml.template)
- [Template de configuracao de seguranca](./templates/security-config-template.java.template)
- [Template de virtual threads](./templates/virtual-threads-config-template.java.template)
- [Template application base](./templates/application.yml.template)
- [Template application development](./templates/application-development.yml.template)
- [Template application local](./templates/application-local.yml.template)
- [Template application production](./templates/application-production.yml.template)
- [Template application test integration](./templates/application-test-integration.yml.template)
- [Template application test e2e](./templates/application-test-e2e.yml.template)
- [Exemplos de build.gradle por modulo](./examples/build-gradle-modules-examples.md)
- [Guia de referencia](../../java-spring-reference.md)
