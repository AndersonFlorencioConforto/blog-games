# Estante do Usuario - API Usage

**Base URL:** `/api/v1`  
**Auth:** `PUBLICA (leitura da estante de terceiros) | USER ou ADMIN (gerenciar propria estante)`  
**Content-Type:** `application/json`

## Objetivo

Permite que usuarios gerenciem sua propria estante de jogos com status `JA_TENHO`, `PRETENDO_PEGAR` ou `FAVORITO`. A estante de qualquer usuario e visivel publicamente.

---

## Endpoints

### GET /users/{userId}/shelf

**Descricao:** Exibe a estante de um usuario publicamente. Suporta filtro por status.  
**Idempotencia:** Sim  
**Auth:** Publica

**Path params:**
- `userId`: UUID do usuario dono da estante

**Parametros de query:**
- `page`: pagina (default: `0`)
- `size`: tamanho da pagina (default: `20`)
- `status`: filtro por status — `JA_TENHO`, `PRETENDO_PEGAR`, `FAVORITO` (opcional)

**Resposta 200:**
```json
{
  "content": [
    {
      "gameId": "g1a2b3c4-0000-0000-0000-000000000001",
      "gameTitle": "The Witcher 3",
      "gameCoverImageUrl": "https://cdn.example.com/covers/witcher3.jpg",
      "status": "JA_TENHO",
      "addedAt": "2026-05-21T10:30:00Z",
      "updatedAt": "2026-05-21T11:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 35,
  "totalPages": 2
}
```

**Erros:**
- `404 NOT_FOUND` - Usuario nao encontrado

---

### POST /shelf

**Descricao:** Adiciona um jogo a propria estante do usuario autenticado.  
**Idempotencia:** Nao (jogo ja na estante retorna 409; use PUT para atualizar status)  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Requisicao:**
```json
{
  "gameId": "g1a2b3c4-0000-0000-0000-000000000001",
  "status": "JA_TENHO"
}
```

**Valores validos para `status`:** `JA_TENHO`, `PRETENDO_PEGAR`, `FAVORITO`

**Resposta 201:**
```json
{
  "gameId": "g1a2b3c4-0000-0000-0000-000000000001",
  "status": "JA_TENHO",
  "addedAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Jogo nao encontrado
- `409 CONFLICT` - Jogo ja esta na estante

---

### PUT /shelf/{gameId}

**Descricao:** Atualiza o status de um jogo ja existente na estante.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `gameId`: UUID do jogo

**Requisicao:**
```json
{
  "status": "FAVORITO"
}
```

**Resposta 200:**
```json
{
  "gameId": "g1a2b3c4-0000-0000-0000-000000000001",
  "status": "FAVORITO",
  "addedAt": "2026-05-21T10:30:00Z",
  "updatedAt": "2026-05-21T12:00:00Z"
}
```

**Erros:**
- `401 UNAUTHORIZED`
- `404 NOT_FOUND` - Jogo nao esta na estante do usuario

---

### DELETE /shelf/{gameId}

**Descricao:** Remove um jogo da propria estante.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `gameId`: UUID do jogo

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `401 UNAUTHORIZED`
- `404 NOT_FOUND` - Jogo nao esta na estante

---

## Cenarios de erro (detalhados)

### 1) Jogo ja na estante

**Status:** `409 CONFLICT`  
**Motivo:** Par (userId, gameId) ja existe na tabela `shelf_items`. Use `PUT /shelf/{gameId}` para atualizar.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/duplicate-entity",
  "title": "Conflict",
  "status": 409,
  "detail": "O jogo 'The Witcher 3' ja esta na sua estante",
  "instance": "/api/v1/shelf",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "abc123"
}
```

### 2) Jogo nao encontrado ao adicionar

**Status:** `404 NOT_FOUND`  
**Motivo:** O `gameId` informado nao existe ou foi deletado.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Game with id 'uuid' not found",
  "instance": "/api/v1/shelf",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "def456"
}
```

### 3) Jogo nao esta na estante ao atualizar/remover

**Status:** `404 NOT_FOUND`  
**Motivo:** Tentativa de `PUT` ou `DELETE` em jogo que nao consta na estante do usuario autenticado.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "ShelfItem for game 'uuid' not found in your shelf",
  "instance": "/api/v1/shelf/uuid",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "ghi789"
}
```

---

## Notas

- A estante e sempre do usuario autenticado (extraido do token JWT) para escrita; nunca de um `userId` do path ou body.
- `GET /users/{userId}/shelf` e publica, permitindo visualizar a estante de qualquer perfil.
- Par `(userId, gameId)` tem constraint UNIQUE na tabela `shelf_items`.
- O status `FAVORITO` pode coexistir com `JA_TENHO` ou `PRETENDO_PEGAR` em outros itens — cada jogo tem um unico item por usuario.
