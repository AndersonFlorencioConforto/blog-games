---
name: spring-rest-endpoint
description: Implementa ou refatora um endpoint REST Spring que delega para casos de uso de application com mapeamento HTTP limpo. Use quando criar ou alterar um controller, expor um novo endpoint REST, definir request e response models, ou mapear status codes e erros HTTP para casos de uso.
argument-hint: "<recurso> <metodo> <path>"
---

# Skill de Endpoint REST Spring

Use esta skill para criar ou atualizar handlers de endpoint sem violar limites de arquitetura.

## Procedimento

1. Defina primeiro o contrato do endpoint na interface `*API` (path, params/body de request, status codes, anotacoes OpenAPI).
2. Crie modelos de request/response (`record`) no pacote de modelos de infrastructure.
3. Adicione mapeamento de presenter de output de application para modelo de response quando necessario.
4. Implemente o controller apenas como orquestracao:
   - parse de entrada
   - construcao de comando/query
   - chamada de um caso de uso
   - mapeamento da resposta
5. Garanta que excecoes de domain/application sejam traduzidas no global exception handler.
6. Adicione ou atualize testes para contrato de endpoint e mapeamento de erro.

## Restricoes obrigatorias

- Sem chamadas de repository no controller.
- Sem regras de negocio no controller.
- Erros de validacao/domain mapeados no limite de infrastructure.
- Mantenha detalhes de serializacao HTTP fora de domain/application.

## Recursos

- [Template de contrato de API](./templates/api-interface-template.java.template)
- [Template de controller](./templates/controller-template.java.template)
- [Template de create request](./templates/create-request-template.java.template)
- [Template de response](./templates/response-template.java.template)
- [Template de presenter](./templates/presenter-template.java.template)
- [Template de global exception handler](./templates/global-exception-handler-template.java.template)
- [Guia de Swagger/OpenAPI com tags](./examples/swagger-openapi-controller-tags.md)
- [Guia de pagination no contrato REST](./examples/pagination-rest-contract-and-controller.md)
