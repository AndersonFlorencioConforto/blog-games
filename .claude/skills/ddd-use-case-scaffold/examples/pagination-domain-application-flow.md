# Pagination no fluxo DDD (domain -> application -> infrastructure)

Este guia documenta como a paginacao esta implementada hoje no projeto e qual padrao deve ser mantido em novas features.

## 1) Contrato de dominio

O dominio centraliza o contrato generico de paginacao:

- `Pagination<T>` com `currentPage`, `perPage`, `total` e `items`.
- Metodo `map(Function<T, R>)` para converter itens sem perder metadados de pagina.
- `SearchQuery` para listagens gerais (`page`, `perPage`, `terms`, `sort`, `direction`).
- `VideoSearchQuery` para listagem de videos com filtros por IDs (`castMembers`, `categories`, `genres`).

Padrao importante:

- Domain nao conhece `Page` do Spring.
- Domain nao conhece anotacoes/framework de serializacao HTTP.
- Domain expoe apenas contratos puros de negocio.

## 2) Application (casos de uso de listagem)

Nos casos de uso `List*UseCase`, a regra observada e:

1. Receber `SearchQuery` (ou `VideoSearchQuery`).
2. Delegar para o gateway do dominio (`findAll(...)`).
3. Converter item de dominio para output com `Pagination.map(...)`.

Exemplo de padrao aplicado:

- `DefaultListCategoriesUseCase`
- `DefaultListGenreUseCase`
- `DefaultListCastMembersUseCase`
- `DefaultListVideosUseCase`

Todos seguem o mesmo formato:

- Sem logica de paginacao no use case.
- Sem acesso direto a repository.
- Sem manipulacao manual de `total/currentPage/perPage`.

## 3) Infrastructure (gateway + JPA)

Nos gateways MySQL a paginacao usa `PageRequest` + `Sort` + filtro por termos:

1. Monta `PageRequest.of(page, perPage, Sort.by(direction, sort))`.
2. Monta filtros opcionais (Specification ou query custom).
3. Executa repository e converte para `Pagination<Aggregate>`.

Padrao observado em:

- `CategoryMySQLGateway.findAll(SearchQuery)`
- `GenreMySQLGateway.findAll(SearchQuery)`
- `CastMemberMySQLGateway.findAll(SearchQuery)`
- `DefaultVideoGateway.findAll(VideoSearchQuery)`

No caso de video:

- Alem de `terms/sort/dir`, converte filtros por IDs e aplica em query SQL/JPA custom.

## 4) API e serializacao da resposta

A API expoe `Pagination<ResponseModel>` diretamente no contrato HTTP.

Como o projeto configura Jackson com `SNAKE_CASE`, os campos saem como:

- `current_page`
- `per_page`
- `total`
- `items`

Isso e validado em testes de API (`CategoryAPITest`, `VideoAPITest`).

## 5) Checklist para novas listagens

Ao criar nova listagem paginada, siga esta ordem:

1. Criar/usar query de dominio (`SearchQuery` ou query especializada).
2. Definir `findAll(query)` no gateway de dominio.
3. Implementar `findAll` no gateway de infrastructure com `PageRequest`.
4. Retornar `Pagination<Aggregate>` do gateway.
5. No use case, converter para output com `page.map(Output::from)`.
6. No controller, montar query a partir dos params e mapear para response.
7. Cobrir testes de:
   - Query/defaults recebidos no endpoint.
   - Campos de paginacao na resposta (`current_page`, `per_page`, `total`, `items`).
   - Ordenacao/filtro/paginacao no gateway.

## 6) Anti-padroes a evitar

- Retornar `Page<T>` de Spring para domain/application.
- Controller montando paginacao manualmente sem usar contrato de dominio.
- Duplicar transformacao de metadados de pagina fora de `Pagination.map`.
- Regras de filtro/sort no controller em vez do gateway.
