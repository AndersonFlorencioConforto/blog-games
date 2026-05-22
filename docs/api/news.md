# Noticias - API Usage

**Base URL:** `/api/v1`  
**Auth:** `PUBLICA (leitura) | ADMIN (escrita)`  
**Content-Type:** `application/json`

## Objetivo

Gerencia noticias editoriais da plataforma, escritas exclusivamente por administradores. Noticias podem ser opcionalmente associadas a um jogo. Todos os usuarios podem ler.

---

## Endpoints

### GET /news

**Descricao:** Lista noticias paginadas, ordenadas por data de publicacao (mais recentes primeiro). Permite filtrar por jogo.  
**Idempotencia:** Sim  
**Auth:** Publica

**Parametros de query:**
- `gameId`: filtrar por jogo relacionado (UUID, opcional)
- `page`: pagina (default: `0`)
- `size`: tamanho da pagina (default: `20`)

**Resposta 200:**
```json
{
  "content": [
    {
      "id": "n1a2b3c4-0000-0000-0000-000000000001",
      "title": "Witcher 4 anunciado!",
      "summary": "CD Projekt Red revela o proximo capitulo da saga Witcher.",
      "coverImageUrl": "https://cdn.example.com/news/witcher4.jpg",
      "relatedGame": {
        "id": "g1a2b3c4-0000-0000-0000-000000000001",
        "title": "The Witcher 4"
      },
      "author": {
        "id": "a0000000-0000-0000-0000-000000000001",
        "name": "Admin"
      },
      "publishedAt": "2026-05-21T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 200,
  "totalPages": 10
}
```

**Nota:** `relatedGame` e `null` se a noticia nao estiver associada a um jogo.

---

### GET /news/{id}

**Descricao:** Retorna o conteudo completo de uma noticia.  
**Idempotencia:** Sim  
**Auth:** Publica

**Path params:**
- `id`: UUID da noticia

**Resposta 200:**
```json
{
  "id": "n1a2b3c4-0000-0000-0000-000000000001",
  "title": "Witcher 4 anunciado!",
  "summary": "CD Projekt Red revela o proximo capitulo da saga Witcher.",
  "content": "Durante a conferencia de imprensa de 2026, a CD Projekt Red revelou oficialmente...",
  "coverImageUrl": "https://cdn.example.com/news/witcher4.jpg",
  "relatedGame": {
    "id": "g1a2b3c4-0000-0000-0000-000000000001",
    "title": "The Witcher 4"
  },
  "author": {
    "id": "a0000000-0000-0000-0000-000000000001",
    "name": "Admin"
  },
  "publishedAt": "2026-05-21T10:30:00Z",
  "updatedAt": "2026-05-21T11:00:00Z"
}
```

**Erros:**
- `404 NOT_FOUND` - Noticia nao encontrada

---

### POST /news

**Descricao:** Cria nova noticia editorial.  
**Idempotencia:** Nao  
**Auth:** ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Requisicao:**
```json
{
  "title": "Witcher 4 anunciado!",
  "summary": "CD Projekt Red revela o proximo capitulo da saga Witcher.",
  "content": "Durante a conferencia de imprensa de 2026...",
  "coverImageUrl": "https://cdn.example.com/news/witcher4.jpg",
  "relatedGameId": "g1a2b3c4-0000-0000-0000-000000000001"
}
```

**Restricoes:**
- `title`: 5–300 chars, obrigatorio
- `summary`: 10–500 chars, obrigatorio
- `content`: min 50 chars, obrigatorio
- `coverImageUrl`: URL valida, opcional
- `relatedGameId`: UUID de jogo existente, opcional

**Resposta 201:** (detalhe completo da noticia criada)

**Erros:**
- `401 UNAUTHORIZED`
- `403 FORBIDDEN` - Nao e ADMIN
- `404 NOT_FOUND` - `relatedGameId` nao existe
- `422 UNPROCESSABLE_ENTITY` - Dados invalidos

---

### PUT /news/{id}

**Descricao:** Atualiza todos os campos de uma noticia existente.  
**Idempotencia:** Sim  
**Auth:** ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID da noticia

**Requisicao:** (mesmo formato do POST)

**Resposta 200:** (detalhe atualizado)

**Erros:**
- `401 UNAUTHORIZED`
- `403 FORBIDDEN`
- `404 NOT_FOUND`
- `422 UNPROCESSABLE_ENTITY`

---

### DELETE /news/{id}

**Descricao:** Remove permanentemente uma noticia.  
**Idempotencia:** Sim  
**Auth:** ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID da noticia

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `401 UNAUTHORIZED`
- `403 FORBIDDEN`
- `404 NOT_FOUND`

---

## Cenarios de erro (detalhados)

### 1) Titulo muito curto

**Status:** `422 UNPROCESSABLE_ENTITY`  
**Motivo:** `title` deve ter entre 5 e 300 caracteres.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/validation-error",
  "title": "Validation Error",
  "status": 422,
  "detail": "O campo 'title' deve ter entre 5 e 300 caracteres",
  "instance": "/api/v1/news",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "abc123"
}
```

### 2) Jogo relacionado nao encontrado

**Status:** `404 NOT_FOUND`  
**Motivo:** O `relatedGameId` informado nao existe ou foi deletado.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Game with id 'uuid' not found",
  "instance": "/api/v1/news",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "def456"
}
```

### 3) Nao autorizado (nao e ADMIN)

**Status:** `403 FORBIDDEN`  
**Motivo:** Apenas administradores podem criar, editar ou remover noticias.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/forbidden",
  "title": "Forbidden",
  "status": 403,
  "detail": "Acesso negado: operacao requer role ADMIN",
  "instance": "/api/v1/news",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "ghi789"
}
```

---

## Notas

- `authorId` e sempre o `userId` extraido do JWT do admin que criou a noticia; nao pode ser informado no body.
- `relatedGame` na resposta e `null` se a noticia nao tiver jogo associado.
- Noticias sao deletadas permanentemente (hard delete), ao contrario de jogos (soft delete).
- Paginacao sempre ordenada por `publishedAt DESC` por default.
