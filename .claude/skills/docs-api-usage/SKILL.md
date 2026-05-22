---
name: docs-api-usage
description: Cria ou atualiza documentacao de uso de funcionalidades e endpoints na pasta docs com payload, response e cenarios de erro. Use quando documentar um endpoint ou feature, registrar exemplos de payload e response, ou atualizar a pasta docs apos implementar um fluxo.
argument-hint: "<feature> <endpoint(s)> <auth/roles>"
---

# Skill de Documentacao de Uso de Funcionalidades e Endpoints

Use esta skill quando criar uma nova funcionalidade, endpoint, ou alterar o contrato de API.

## Entradas esperadas

- Nome da feature/recurso.
- Metodos e paths dos endpoints.
- Auth/roles e requisitos de seguranca.
- Requisicao (payload, query params, path params, headers).
- Respostas e status codes (sucesso e erro).
- Regras especiais (idempotencia, rate limit, paginacao).

## Procedimento

1. Defina o alvo de documentacao:
   - Para endpoints REST: crie um doc dedicado em `docs/api/<feature>.md` (crie a pasta se nao existir).
   - Atualize `docs/architecture/api-catalog.md` com resumo e link para o doc dedicado.
2. Use o template desta skill para estruturar o conteudo.
3. Inclua exemplos completos de payload e response em JSON.
4. Liste pelo menos 3 cenarios de erro com status HTTP e exemplo RFC 7807.
5. Para endpoints com query/headers, documente parametros, defaults e exemplos.
6. Garanta que a doc reflete o contrato real implementado (campos, status, regras).

## Restricoes obrigatorias

- Documentacao sempre em `docs/`.
- Sempre incluir:
  - Exemplo de payload
  - Exemplo de response
  - Cenarios de erro com response RFC 7807
- Nao omitir status codes ou regras de negocio relevantes.
- Se houver mais de um endpoint, documente cada um separadamente.

## Formato de saida

- Criar/atualizar arquivo em `docs/api/<feature>.md`.
- Atualizar resumo em `docs/architecture/api-catalog.md`.

## Recursos

- [Template de doc de endpoint](./templates/endpoint-doc.md.template)
