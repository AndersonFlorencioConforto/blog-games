# Jogos e Avaliacoes - API Usage

**Base URL:** `/api/v1`  
**Auth:** `PUBLICA (leitura) | ADMIN (escrita) | USER ou ADMIN (avaliacoes)`  
**Content-Type:** `application/json`

## Objetivo

Catalogo de jogos gerenciado exclusivamente por administradores (CRUD). Usuarios autenticados podem avaliar jogos com 0–5 estrelas. A media de avaliacoes e desnormalizada no jogo via pessimistic lock.

---

## Endpoints

### GET /games

**Descricao:** Lista jogos ativos (nao deletados) com filtros opcionais por categoria e plataforma.  
**Idempotencia:** Sim  
**Auth:** Publica

**Parametros de query:**
- `category`: filtro por categoria (ex: `RPG`, `ACAO`) — opcional
- `platform`: filtro por plataforma (ex: `PC`, `PLAYSTATION`) — opcional
- `page`: pagina (default: `0`)
- `size`: tamanho da pagina (default: `20`)
- `sort`: campo de ordenacao (default: `createdAt,desc`)

**Valores validos para `category`:** `ACAO`, `RPG`, `LUTA`, `ESPORTE`, `AVENTURA`, `ESTRATEGIA`, `SIMULACAO`, `TERROR`, `PLATAFORMA`, `CORRIDA`, `PUZZLE`, `OUTROS`

**Valores validos para `platform`:** `PC`, `PLAYSTATION`, `XBOX`, `NINTENDO`, `MOBILE`, `OUTROS`

**Resposta 200:**
```json
{
  "content": [
    {
      "id": "g1a2b3c4-0000-0000-0000-000000000001",
      "title": "The Witcher 3",
      "coverImageUrl": "https://cdn.example.com/covers/witcher3.jpg",
      "platformScore": 9.5,
      "userAverageRating": 4.7,
      "totalRatings": 1203,
      "categories": ["RPG", "ACAO"],
      "platforms": ["PC", "PLAYSTATION"],
      "createdAt": "2026-05-21T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}
```

---

### GET /games/{id}

**Descricao:** Retorna detalhes completos de um jogo, incluindo review editorial, pros e cons.  
**Idempotencia:** Sim  
**Auth:** Publica

**Path params:**
- `id`: UUID do jogo

**Resposta 200:**
```json
{
  "id": "g1a2b3c4-0000-0000-0000-000000000001",
  "title": "The Witcher 3",
  "description": "RPG de mundo aberto ambientado num universo dark fantasy...",
  "editorialReview": "Uma obra-prima do genero RPG...",
  "platformScore": 9.5,
  "userAverageRating": 4.7,
  "totalRatings": 1203,
  "pros": ["Historia incrivel", "Mundo vasto", "Personagens memoraveis"],
  "cons": ["Pode ser lento no inicio"],
  "coverImageUrl": "https://cdn.example.com/covers/witcher3.jpg",
  "categories": ["RPG", "ACAO"],
  "platforms": ["PC", "PLAYSTATION", "XBOX"],
  "createdAt": "2026-05-21T10:30:00Z",
  "updatedAt": "2026-05-21T11:00:00Z"
}
```

**Erros:**
- `404 NOT_FOUND` - Jogo nao encontrado ou deletado

---

### POST /games

**Descricao:** Cadastra novo jogo. Titulo deve ser unico.  
**Idempotencia:** Nao  
**Auth:** ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Requisicao:**
```json
{
  "title": "The Witcher 3",
  "description": "RPG de mundo aberto...",
  "editorialReview": "Uma obra-prima do genero...",
  "platformScore": 9.5,
  "pros": ["Historia incrivel", "Mundo vasto"],
  "cons": ["Pode ser lento no inicio"],
  "coverImageUrl": "https://cdn.example.com/covers/witcher3.jpg",
  "categories": ["RPG", "ACAO"],
  "platforms": ["PC", "PLAYSTATION"]
}
```

**Restricoes:**
- `title`: 1–200 chars, obrigatorio, unico
- `description`: 1–5000 chars, obrigatorio
- `editorialReview`: max 10000 chars, opcional
- `platformScore`: 0.0–10.0, duas casas decimais
- `pros` / `cons`: max 10 itens, cada um max 200 chars, opcional
- `categories`: ao menos 1 valor valido, obrigatorio
- `platforms`: ao menos 1 valor valido, obrigatorio

**Resposta 201:** (detalhe completo do jogo criado)

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `403 FORBIDDEN` - Nao e ADMIN
- `409 CONFLICT` - Titulo ja cadastrado
- `422 UNPROCESSABLE_ENTITY` - Dados invalidos

---

### PUT /games/{id}

**Descricao:** Atualiza todos os campos de um jogo existente.  
**Idempotencia:** Sim  
**Auth:** ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID do jogo

**Requisicao:** (mesmo formato do POST)

**Resposta 200:** (detalhe atualizado)

**Erros:**
- `401 UNAUTHORIZED`
- `403 FORBIDDEN`
- `404 NOT_FOUND`
- `422 UNPROCESSABLE_ENTITY`

---

### DELETE /games/{id}

**Descricao:** Soft delete do jogo (campo `deletedAt` preenchido). Jogo desaparece das listagens publicas mas historico de avaliacoes e preservado.  
**Idempotencia:** Sim  
**Auth:** ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID do jogo

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `401 UNAUTHORIZED`
- `403 FORBIDDEN`
- `404 NOT_FOUND`

---

### POST /games/{id}/ratings

**Descricao:** Avalia um jogo com 0–5 estrelas. Se ja avaliou, atualiza a avaliacao existente. Media e atualizada na mesma transacao com pessimistic lock.  
**Idempotencia:** Sim (segunda chamada atualiza avaliacao existente)  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID do jogo

**Requisicao:**
```json
{
  "stars": 4
}
```

**Restricoes:**
- `stars`: inteiro 0–5, obrigatorio

**Resposta 200:**
```json
{
  "gameId": "g1a2b3c4-0000-0000-0000-000000000001",
  "userId": "a1b2c3d4-0000-0000-0000-000000000001",
  "stars": 4,
  "ratedAt": "2026-05-21T10:30:00Z",
  "newAverage": 4.72,
  "totalRatings": 1204
}
```

**Erros:**
- `400 BAD_REQUEST` - Stars fora do range 0–5
- `401 UNAUTHORIZED`
- `404 NOT_FOUND` - Jogo nao encontrado ou deletado

---

### GET /games/{id}/ratings/me

**Descricao:** Retorna a avaliacao do usuario autenticado para o jogo.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID do jogo

**Resposta 200:**
```json
{
  "stars": 4,
  "ratedAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `401 UNAUTHORIZED`
- `404 NOT_FOUND` - Usuario nao avaliou este jogo

---

## Cenarios de erro (detalhados)

### 1) Titulo de jogo duplicado

**Status:** `409 CONFLICT`  
**Motivo:** Ja existe um jogo com o mesmo titulo.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/duplicate-entity",
  "title": "Conflict",
  "status": 409,
  "detail": "Jogo com titulo 'The Witcher 3' ja esta cadastrado",
  "instance": "/api/v1/games",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "abc123"
}
```

### 2) Nenhuma categoria informada

**Status:** `422 UNPROCESSABLE_ENTITY`  
**Motivo:** O campo `categories` e obrigatorio e deve ter ao menos 1 item valido.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/validation-error",
  "title": "Validation Error",
  "status": 422,
  "detail": "Ao menos uma categoria e obrigatoria",
  "instance": "/api/v1/games",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "def456"
}
```

### 3) Stars fora do range

**Status:** `400 BAD_REQUEST`  
**Motivo:** Valor de `stars` nao esta entre 0 e 5.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/invalid-range",
  "title": "Bad Request",
  "status": 400,
  "detail": "Stars deve ser entre 0 e 5, recebido: 6",
  "instance": "/api/v1/games/uuid/ratings",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "ghi789"
}
```

### 4) Nao autorizado (nao e ADMIN)

**Status:** `403 FORBIDDEN`  
**Motivo:** Endpoint de escrita de jogos exige role ADMIN.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/forbidden",
  "title": "Forbidden",
  "status": 403,
  "detail": "Acesso negado: operacao requer role ADMIN",
  "instance": "/api/v1/games",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "jkl012"
}
```

---

## Notas

- `userAverageRating` e `totalRatings` sao desnormalizados no jogo; atualizados via `AVG(stars)` + `COUNT(*)` com `PESSIMISTIC_WRITE` lock na mesma transacao do rating.
- Soft delete: campo `deletedAt` nullable; jogos deletados nao aparecem em GET /games nem em GET /games/{id}.
- `pros` e `cons` sao armazenados como `@ElementCollection` (tabela separada sem ID proprio).
- `platformScore` representa a nota editorial da plataforma (0.0–10.0); `userAverageRating` e a media das notas dos usuarios (0.00–5.00).
