---
name: ddd-use-case-scaffold
description: Faz scaffold de um fluxo completo de caso de uso (domain/application/infrastructure/tests) seguindo arquitetura em camadas e convencoes de nomenclatura. Use quando criar um novo caso de uso ou agregado ponta a ponta, adicionar entidade, value object ou porta, ou evoluir uma operacao de negocio (Create, Update, Delete, GetById) respeitando os limites de camada.
argument-hint: "<recurso> <acao> [restricoes]"
---

# Skill de Scaffold de Caso de Uso DDD

Use esta skill quando precisar criar ou evoluir uma operacao de negocio ponta a ponta com limites arquiteturais seguros.

## Entradas esperadas

- Nome do recurso (por exemplo: Order, Customer, Invoice)
- Acao (por exemplo: Create, Update, Delete, GetById)
- Restricoes (validacao, regras de negocio, efeitos colaterais)

## Procedimento

1. Descubra convencoes existentes de nomenclatura de modulo e pacote.
2. Crie/atualize primeiro contratos e comportamento do domain.
3. Defina `XID extends Identifier` com `unique()`, `from(String)` e `getValue()`.
4. Defina validacao de dominio em `XValidator extends Validator` e delegue no agregado via `validate(handler)`.
5. Crie classes de comando, output e caso de uso em application.
6. Em `application`, para comandos com colecoes de IDs, trate `null` como vazio antes de `stream()`.
7. Em `application`, para busca por ID, use `XID.from(input)` e `NotFoundException.with(X.class, xId)` com o ID correto do agregado.
8. Crie contrato HTTP e mapeamento de borda em infrastructure (`*API`, request/response models, presenter, controller).
9. Implemente adapter de persistencia (`XMySQLGateway`, `XJpaEntity`, `XRepository`) e mapeamentos (`from`, `toAggregate`).
10. Faça o wiring de beans de caso de uso na configuracao de infrastructure quando wiring explicito for utilizado.
11. Adicione testes unitarios de caso de uso/domain e testes de integracao de adapter de infrastructure quando o comportamento de persistencia mudar.
12. Execute o comando de teste direcionado e reporte o resultado.

## Verificacoes obrigatorias

- Domain nao importa APIs de framework.
- `XID` segue padrao consistente (`unique`, `from`, `getValue`).
- Controller nao chama repository diretamente.
- Nomenclatura de caso de uso usa `DefaultXUseCase`.
- Gateway de infrastructure implementa interface de gateway de domain/application.
- Entidade JPA permanece como modelo de persistencia e converte via `from`/`toAggregate`.
- Conversao de colecoes de IDs em command e null-safe.
- NotFound usa o ID do agregado do proprio caso de uso.
- Estrategia de erro no caso de uso usa excecoes de dominio previsiveis (`NotificationException`, `NotFoundException`, `DomainException`) e e consistente no fluxo.
- Cobertura de testes inclui caminho feliz + caminho de falha.

## Recursos

- [Template default create use case](./templates/default-create-usecase.java.template)
- [Template de teste default create use case](./templates/default-create-usecase-test.java.template)
- [Template de gateway MySQL](./templates/mysql-gateway-template.java.template)
- [Template de entidade JPA](./templates/jpa-entity-template.java.template)
- [Template de repository](./templates/repository-template.java.template)
- [Template de configuracao de caso de uso](./templates/usecase-config-template.java.template)
- [Guia de paginacao (domain/application/infrastructure)](./examples/pagination-domain-application-flow.md)
