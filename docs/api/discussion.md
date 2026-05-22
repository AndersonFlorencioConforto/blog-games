# Discussion (Threads, Replies, Likes) - API Usage

**Base URL:** `/api/v1`  
**Auth:** `PUBLICA (leitura) | USER ou ADMIN (escrita e likes)`  
**Content-Type:** `application/json`

## Objetivo

Permite que usuarios abram threads de discussao em jogos, respondam a threads abertas e dem/removam likes em threads e respostas. Administradores e usuarios comuns podem interagir igualmente. Leituras sao publicas.

---

## Endpoints

### GET /games/{gameId}/threads

**Descricao:** Lista threads de um jogo paginadas por data de criacao (mais recentes primeiro).  
**Idempotencia:** Sim  
**Auth:** Publica

**Path params:**
- `gameId`: UUID do jogo

**Parametros de query:**
- `page`: pagina (default: `0`)
- `size`: tamanho da pagina (default: `20`)
- `sort`: campo de ordenacao (default: `createdAt,desc`)

**Resposta 200:**
```json
{
  "content": [
    {
      "id": "f1a2b3c4-0000-0000-0000-000000000001",
      "title": "Qual a melhor build para guerreiro?",
      "authorId": "a1b2c3d4-0000-0000-0000-000000000001",
      "likeCount": 8,
      "replyCount": 15,
      "createdAt": "2026-05-21T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 45,
  "totalPages": 3
}
```

**Erros:**
- `404 NOT_FOUND` - Jogo nao encontrado

---

### POST /games/{gameId}/threads

**Descricao:** Abre uma nova thread em um jogo. Usuario nao pode estar banido.  
**Idempotencia:** Nao  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `gameId`: UUID do jogo

**Requisicao:**
```json
{
  "title": "Qual a melhor build para guerreiro?",
  "content": "Estou pensando em focar em forca mas vi builds de agilidade que parecem fortes..."
}
```

**Restricoes:**
- `title`: 5–200 caracteres, obrigatorio
- `content`: 10–5000 caracteres, obrigatorio

**Resposta 201:**
```json
{
  "id": "f1a2b3c4-0000-0000-0000-000000000001",
  "gameId": "00000000-0000-0000-0000-000000000099",
  "authorId": "a1b2c3d4-0000-0000-0000-000000000001",
  "title": "Qual a melhor build para guerreiro?",
  "content": "Estou pensando em focar em forca...",
  "likeCount": 0,
  "replyCount": 0,
  "createdAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Jogo nao encontrado
- `422 UNPROCESSABLE_ENTITY` - Titulo ou conteudo fora do range

---

### GET /games/{gameId}/threads/{threadId}

**Descricao:** Retorna detalhe da thread com respostas paginadas.  
**Idempotencia:** Sim  
**Auth:** Publica

**Path params:**
- `gameId`: UUID do jogo
- `threadId`: UUID da thread

**Parametros de query (para replies):**
- `replyPage`: pagina de respostas (default: `0`)
- `replySize`: tamanho da pagina de respostas (default: `20`)

**Resposta 200:**
```json
{
  "id": "f1a2b3c4-0000-0000-0000-000000000001",
  "gameId": "00000000-0000-0000-0000-000000000099",
  "authorId": "a1b2c3d4-0000-0000-0000-000000000001",
  "title": "Qual a melhor build para guerreiro?",
  "content": "Estou pensando em focar em forca...",
  "likeCount": 8,
  "replyCount": 15,
  "createdAt": "2026-05-21T10:30:00Z",
  "updatedAt": "2026-05-21T11:00:00Z",
  "replies": {
    "content": [
      {
        "id": "r1b2c3d4-0000-0000-0000-000000000001",
        "threadId": "f1a2b3c4-0000-0000-0000-000000000001",
        "authorId": "b2c3d4e5-0000-0000-0000-000000000001",
        "content": "Eu uso forca + destreza e funciona bem nos primeiros atos.",
        "likeCount": 3,
        "createdAt": "2026-05-21T10:45:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 15,
    "totalPages": 1
  }
}
```

**Erros:**
- `404 NOT_FOUND` - Thread nao encontrada

---

### POST /games/{gameId}/threads/{threadId}/replies

**Descricao:** Adiciona uma resposta a uma thread. Incrementa `replyCount` na thread.  
**Idempotencia:** Nao  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `gameId`: UUID do jogo
- `threadId`: UUID da thread

**Requisicao:**
```json
{
  "content": "Eu prefiro focar em agilidade — o dano critico compensa muito melhor."
}
```

**Restricoes:**
- `content`: 1–2000 caracteres, obrigatorio

**Resposta 201:**
```json
{
  "id": "r1b2c3d4-0000-0000-0000-000000000002",
  "threadId": "f1a2b3c4-0000-0000-0000-000000000001",
  "authorId": "a1b2c3d4-0000-0000-0000-000000000001",
  "content": "Eu prefiro focar em agilidade...",
  "likeCount": 0,
  "createdAt": "2026-05-21T10:50:00Z"
}
```

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Thread nao encontrada
- `422 UNPROCESSABLE_ENTITY` - Conteudo invalido (vazio ou muito longo)

---

### POST /games/{gameId}/threads/{threadId}/likes

**Descricao:** Adiciona like do usuario autenticado a uma thread. Idempotente — se ja curtiu, nao duplica.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `gameId`: UUID do jogo
- `threadId`: UUID da thread

**Requisicao:** vazia

**Resposta:** `204 NO_CONTENT`

**Efeito colateral:** Incrementa `likeCount` na thread (apenas no primeiro like; chamadas subsequentes sao no-op).

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Thread nao encontrada

---

### DELETE /games/{gameId}/threads/{threadId}/likes

**Descricao:** Remove like do usuario autenticado de uma thread. Idempotente — se nao curtiu, e no-op.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `gameId`: UUID do jogo
- `threadId`: UUID da thread

**Resposta:** `204 NO_CONTENT`

**Efeito colateral:** Decrementa `likeCount` na thread (minimo 0).

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Thread nao encontrada

---

### POST /games/{gameId}/threads/{threadId}/replies/{replyId}/likes

**Descricao:** Adiciona like do usuario autenticado a uma resposta. Idempotente.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `gameId`: UUID do jogo
- `threadId`: UUID da thread
- `replyId`: UUID da resposta

**Requisicao:** vazia

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Resposta nao encontrada

---

### DELETE /games/{gameId}/threads/{threadId}/replies/{replyId}/likes

**Descricao:** Remove like do usuario autenticado de uma resposta. Idempotente.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `gameId`: UUID do jogo
- `threadId`: UUID da thread
- `replyId`: UUID da resposta

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Resposta nao encontrada

---

## Cenarios de erro (detalhados)

### 1) Titulo da thread muito curto

**Status:** `422 UNPROCESSABLE_ENTITY`  
**Motivo:** `title` deve ter entre 5 e 200 caracteres.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/validation-error",
  "title": "Validation Error",
  "status": 422,
  "detail": "O campo 'title' deve ter entre 5 e 200 caracteres",
  "instance": "/api/v1/games/uuid/threads",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "abc123"
}
```

### 2) Jogo nao encontrado ao criar thread

**Status:** `404 NOT_FOUND`  
**Motivo:** O `gameId` informado nao existe ou foi deletado (soft delete).  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Game with id 'uuid' not found",
  "instance": "/api/v1/games/uuid/threads",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "def456"
}
```

### 3) Thread nao encontrada ao responder

**Status:** `404 NOT_FOUND`  
**Motivo:** O `threadId` informado nao existe.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Thread with id 'uuid' not found",
  "instance": "/api/v1/games/uuid/threads/uuid/replies",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "ghi789"
}
```

### 4) Token ausente ou invalido

**Status:** `401 UNAUTHORIZED`  
**Motivo:** Endpoint protegido chamado sem `Authorization: Bearer <token>` valido.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Token de acesso ausente ou invalido",
  "instance": "/api/v1/games/uuid/threads",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "jkl012"
}
```

---

## Notas

- `likeCount` e `replyCount` sao campos desnormalizados na tabela `threads`; `likeCount` e desnormalizado em `replies`. Sao atualizados na mesma transacao do like/reply.
- Likes sao armazenados em tabelas separadas (`thread_likes`, `reply_likes`) com chave primaria composta `(threadId, userId)` e `(replyId, userId)`, garantindo unicidade por usuario.
- `gameId` no path de threads e replies e usado apenas para contexto de roteamento; a validacao de existencia do jogo ocorre somente na criacao da thread (POST).
- Threads e respostas nao possuem soft delete nesta versao; deletar requer slice de Moderacao.
- Paginacao de respostas no detalhe da thread: query params `replyPage` e `replySize`.
