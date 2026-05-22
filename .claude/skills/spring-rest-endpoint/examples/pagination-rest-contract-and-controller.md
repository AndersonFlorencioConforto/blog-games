# Pagination em contratos REST e controllers Spring

Este guia padroniza como expor endpoints paginados no projeto, mantendo controller enxuto e contrato HTTP consistente.

## 1) Contrato de endpoint (interface \*API)

O padrao atual usa query params com defaults explicitos:

- `search` (default `""`)
- `page` (default `"0"`)
- `perPage` (default varia por recurso: `10` ou `25`)
- `sort` (default por recurso, ex.: `name` ou `title`)
- `dir` (default `asc`)

Para videos, ha filtros adicionais:

- `cast_members_ids`
- `categories_ids`
- `genres_ids`

Recomendacoes:

- Declarar defaults no contrato da interface `*API`.
- Reaproveitar nomes de query params ja usados no projeto para evitar quebra de cliente.
- Documentar no OpenAPI (`@Operation`, `@ApiResponses`) que a resposta e paginada.

## 2) Controller enxuto

Padrao aplicado nos controllers:

1. Ler params HTTP.
2. Montar `SearchQuery` ou `VideoSearchQuery`.
3. Chamar `List*UseCase.execute(...)`.
4. Mapear itens com presenter e devolver `Pagination<ResponseModel>`.

Exemplos atuais:

- `CategoryController.listCategories(...)`
- `GenreController.list(...)`
- `CastMemberController.list(...)`
- `VideoController.list(...)`

## 3) Conversao de IDs de filtro (caso Video)

No endpoint de videos, os filtros chegam como `Set<String>` e devem ser convertidos para IDs de dominio antes de chamar o use case:

- `CastMemberID.from(...)`
- `CategoryID.from(...)`
- `GenreID.from(...)`

Isso mantem validacoes e semantica de ID dentro do modelo de dominio e evita stringly typed logic na aplicacao.

## 4) Formato de resposta JSON

A resposta usa `Pagination<ResponseModel>` e o ObjectMapper global esta em `SNAKE_CASE`.

Portanto, o JSON final deve usar:

- `current_page`
- `per_page`
- `total`
- `items`

Esse formato e parte do contrato e deve ser preservado para compatibilidade.

## 5) Testes obrigatorios para endpoint paginado

Para cada novo endpoint paginado, cobrir no minimo:

1. Params explicitos:
   - garante que query recebida no use case contem `page/perPage/search/sort/dir` esperados.
2. Defaults:
   - garante aplicacao correta dos defaults quando params nao sao enviados.
3. Payload de resposta:
   - valida `current_page`, `per_page`, `total` e `items`.
4. Filtros extras (quando houver):
   - valida mapeamento de strings para IDs de dominio.

Referencias reais no projeto:

- `CategoryAPITest` (listagem e contrato JSON)
- `VideoAPITest` (listagem com filtros por IDs e defaults)

## 6) Checklist rapido de review

- Contrato `*API` define query params e defaults.
- Controller nao acessa repository diretamente.
- Controller apenas monta query + delega para use case + presenter.
- Use case retorna `Pagination<Output>`.
- Resposta serializa em `snake_case`.
- Testes cobrem caminho feliz + defaults + filtros.
