# Catalogo de API - Blog Games Platform

**Versao:** 1.0  
**Base URL:** `/api/v1`  
**Autenticacao:** Bearer JWT no header `Authorization`  
**Content-Type:** `application/json`

---

## Convencoes

- Todos os IDs sao UUID v4
- Timestamps em ISO-8601 UTC: `2026-05-21T10:30:00Z`
- Paginacao padrao: `page=0&size=20&sort=createdAt,desc`
- Erros seguem RFC 7807 (Problem Details)

### Formato de Erro Padrao

```json
{
  "type": "https://blogames.com/errors/validation-error",
  "title": "Validation Error",
  "status": 422,
  "detail": "O campo 'email' e obrigatorio",
  "instance": "/api/v1/auth/register",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "abc123"
}
```

---

## 1. Autenticacao (`/auth`)

> **Documentacao detalhada:** [`docs/api/auth.md`](../api/auth.md)

### POST /auth/register
**Descricao:** Cadastro de novo usuario  
**Auth:** Publica  
**Idempotencia:** Nao (email unico; repetir com mesmo email retorna 409)

**Request:**
```json
{
  "name": "Anderson Conforto",
  "email": "anderson@email.com",
  "password": "MinhaSenh@123"
}
```

**Response 201:**
```json
{
  "id": "uuid",
  "name": "Anderson Conforto",
  "email": "anderson@email.com",
  "role": "USER",
  "createdAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `409 CONFLICT` - Email ja cadastrado
- `422 UNPROCESSABLE_ENTITY` - Dados invalidos (senha fraca, email malformado)

---

### POST /auth/login
**Descricao:** Autenticacao e obtencao de tokens  
**Auth:** Publica  
**Rate Limit:** 10 req/min por IP

**Request:**
```json
{
  "email": "anderson@email.com",
  "password": "MinhaSenh@123"
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbG...",
  "refreshToken": "uuid-opaco",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Erros:**
- `401 UNAUTHORIZED` - Credenciais invalidas
- `403 FORBIDDEN` - Usuario banido (inclui `bannedUntil` no detail)
- `429 TOO_MANY_REQUESTS` - Rate limit excedido

---

### POST /auth/refresh
**Descricao:** Renovar access token usando refresh token  
**Auth:** Publica (refresh token no body)

**Request:**
```json
{
  "refreshToken": "uuid-opaco"
}
```

**Response 200:** (mesmo formato do login)

**Erros:**
- `401 UNAUTHORIZED` - Refresh token invalido ou expirado

---

### POST /auth/logout
**Descricao:** Invalida o access token atual e o refresh token  
**Auth:** Autenticado (USER, ADMIN)

**Request:** vazio (token extraido do header)

**Response:** `204 NO_CONTENT`

---

### POST /auth/forgot-password
**Descricao:** Solicita recuperacao de senha; envia e-mail com link  
**Auth:** Publica  
**Observacao:** Sempre retorna 202 para nao revelar se email existe

**Request:**
```json
{
  "email": "anderson@email.com"
}
```

**Response:** `202 ACCEPTED`

---

### POST /auth/reset-password
**Descricao:** Redefine senha usando token recebido por e-mail  
**Auth:** Publica

**Request:**
```json
{
  "token": "uuid-do-email",
  "newPassword": "NovaSenha@456"
}
```

**Response:** `204 NO_CONTENT`

**Erros:**
- `400 BAD_REQUEST` - Token invalido, expirado ou ja usado
- `422 UNPROCESSABLE_ENTITY` - Senha nao atende politica

---

## 2. Usuarios (`/users`)

> **Documentacao detalhada:** [`docs/api/users.md`](../api/users.md)

### GET /users/{id}
**Descricao:** Exibe perfil publico de um usuario  
**Auth:** Publica

**Response 200:**
```json
{
  "id": "uuid",
  "name": "Anderson Conforto",
  "bio": "Gamer desde 1995",
  "avatarUrl": "https://cdn.example.com/avatars/uuid.jpg",
  "followersCount": 42,
  "followingCount": 18,
  "createdAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `404 NOT_FOUND` - Usuario nao encontrado

---

### PUT /users/me
**Descricao:** Atualiza perfil do usuario autenticado  
**Auth:** USER, ADMIN

**Request:**
```json
{
  "name": "Anderson Conforto",
  "bio": "Gamer desde 1995",
  "avatarUrl": "https://cdn.example.com/avatars/uuid.jpg"
}
```

**Response 200:** (perfil atualizado, mesmo formato de GET /users/{id})

**Erros:**
- `422 UNPROCESSABLE_ENTITY` - Dados invalidos

---

### GET /users/me
**Descricao:** Retorna dados do usuario autenticado (incluindo email e role)  
**Auth:** USER, ADMIN

**Response 200:**
```json
{
  "id": "uuid",
  "name": "Anderson Conforto",
  "email": "anderson@email.com",
  "bio": "Gamer desde 1995",
  "avatarUrl": "https://...",
  "role": "USER",
  "followersCount": 42,
  "followingCount": 18,
  "createdAt": "2026-05-21T10:30:00Z"
}
```

---

### POST /users/{id}/follow
**Descricao:** Seguir um usuario  
**Auth:** USER, ADMIN  
**Idempotencia:** Sim (seguir ja seguido nao gera erro; retorna 200)

**Response:** `204 NO_CONTENT`

**Erros:**
- `400 BAD_REQUEST` - Tentar seguir a si mesmo
- `404 NOT_FOUND` - Usuario nao encontrado

---

### DELETE /users/{id}/follow
**Descricao:** Deixar de seguir um usuario  
**Auth:** USER, ADMIN

**Response:** `204 NO_CONTENT`

---

### GET /users/{id}/followers
**Descricao:** Lista seguidores de um usuario  
**Auth:** Publica  
**Paginacao:** `page`, `size`

**Response 200:**
```json
{
  "content": [{ "id": "uuid", "name": "...", "avatarUrl": "..." }],
  "page": 0,
  "size": 20,
  "totalElements": 42,
  "totalPages": 3
}
```

---

### GET /users/{id}/following
**Descricao:** Lista usuarios que um usuario segue  
**Auth:** Publica  
**Paginacao:** `page`, `size`

**Response 200:** (mesmo formato de /followers)

---

### POST /users/{id}/report
**Descricao:** Denunciar um usuario  
**Auth:** USER, ADMIN

**Request:**
```json
{
  "reason": "ABUSIVE_BEHAVIOR",
  "description": "Usuario enviou mensagens ofensivas na thread do jogo X"
}
```

**Response:** `201 CREATED`

**Erros:**
- `400 BAD_REQUEST` - Tentar denunciar a si mesmo
- `404 NOT_FOUND` - Usuario nao encontrado
- `422 UNPROCESSABLE_ENTITY` - Reason invalido

---

## 3. Jogos (`/games`)

> **Documentacao detalhada:** [`docs/api/games.md`](../api/games.md)

### GET /games
**Descricao:** Lista jogos com filtros opcionais  
**Auth:** Publica  
**Query params:** `category`, `platform`, `page`, `size`, `sort`

**Exemplo:** `GET /games?category=RPG&platform=PC&page=0&size=20`

**Response 200:**
```json
{
  "content": [
    {
      "id": "uuid",
      "title": "The Witcher 3",
      "coverImageUrl": "https://...",
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
**Descricao:** Detalhe completo de um jogo  
**Auth:** Publica

**Response 200:**
```json
{
  "id": "uuid",
  "title": "The Witcher 3",
  "description": "RPG de mundo aberto...",
  "editorialReview": "Review completo...",
  "platformScore": 9.5,
  "userAverageRating": 4.7,
  "totalRatings": 1203,
  "pros": ["Historia incrivel", "Mundo vasto"],
  "cons": ["Pode ser lento no inicio"],
  "coverImageUrl": "https://...",
  "categories": ["RPG", "ACAO"],
  "platforms": ["PC", "PLAYSTATION", "XBOX"],
  "createdAt": "2026-05-21T10:30:00Z",
  "updatedAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `404 NOT_FOUND`

---

### POST /games
**Descricao:** Cadastra novo jogo  
**Auth:** ADMIN

**Request:**
```json
{
  "title": "The Witcher 3",
  "description": "RPG de mundo aberto...",
  "editorialReview": "Review completo...",
  "platformScore": 9.5,
  "pros": ["Historia incrivel"],
  "cons": ["Pode ser lento"],
  "coverImageUrl": "https://...",
  "categories": ["RPG", "ACAO"],
  "platforms": ["PC", "PLAYSTATION"]
}
```

**Response 201:** (detalhe completo do jogo criado)

**Erros:**
- `403 FORBIDDEN` - Nao e ADMIN
- `409 CONFLICT` - Jogo com mesmo titulo ja existe
- `422 UNPROCESSABLE_ENTITY` - Dados invalidos

---

### PUT /games/{id}
**Descricao:** Atualiza jogo existente  
**Auth:** ADMIN

**Request:** (mesmo formato do POST, todos os campos)

**Response 200:** (detalhe atualizado)

**Erros:**
- `403 FORBIDDEN`
- `404 NOT_FOUND`
- `422 UNPROCESSABLE_ENTITY`

---

### DELETE /games/{id}
**Descricao:** Remove jogo (soft delete ou hard delete - ver premissa P-013)  
**Auth:** ADMIN

**Response:** `204 NO_CONTENT`

**Erros:**
- `403 FORBIDDEN`
- `404 NOT_FOUND`

**Premissa P-013:** Soft delete preferivel para preservar historico de avaliacoes. Campo `deletedAt` nullable. Decisao a confirmar antes de implementar.

---

### POST /games/{id}/ratings
**Descricao:** Avaliar um jogo (criar ou atualizar avaliacao propria)  
**Auth:** USER, ADMIN  
**Idempotencia:** Sim (segunda chamada atualiza avaliacao existente)

**Request:**
```json
{
  "stars": 4
}
```

**Response 200:**
```json
{
  "gameId": "uuid",
  "userId": "uuid",
  "stars": 4,
  "ratedAt": "2026-05-21T10:30:00Z",
  "newAverage": 4.72,
  "totalRatings": 1204
}
```

**Erros:**
- `400 BAD_REQUEST` - Stars fora do range 0-5
- `404 NOT_FOUND` - Jogo nao encontrado

---

### GET /games/{id}/ratings/me
**Descricao:** Retorna a avaliacao do usuario autenticado para um jogo  
**Auth:** USER, ADMIN

**Response 200:**
```json
{
  "stars": 4,
  "ratedAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `404 NOT_FOUND` - Usuario nao avaliou este jogo

---

## 4. Estante do Usuario (`/users/{userId}/shelf` e `/shelf`)

> **Documentacao detalhada:** [`docs/api/shelf.md`](../api/shelf.md)

### GET /users/{userId}/shelf
**Descricao:** Exibe estante de um usuario (publica)  
**Auth:** Publica  
**Paginacao:** `page`, `size`, `status` (filtro opcional)

**Response 200:**
```json
{
  "content": [
    {
      "game": {
        "id": "uuid",
        "title": "The Witcher 3",
        "coverImageUrl": "https://..."
      },
      "status": "JA_TENHO",
      "addedAt": "2026-05-21T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 35,
  "totalPages": 2
}
```

---

### POST /shelf
**Descricao:** Adiciona jogo a propria estante  
**Auth:** USER, ADMIN

**Request:**
```json
{
  "gameId": "uuid",
  "status": "JA_TENHO"
}
```

**Valores validos para status:** `JA_TENHO`, `PRETENDO_PEGAR`, `FAVORITO`

**Response 201:**
```json
{
  "gameId": "uuid",
  "status": "JA_TENHO",
  "addedAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `404 NOT_FOUND` - Jogo nao encontrado
- `409 CONFLICT` - Jogo ja na estante (use PUT para atualizar)

---

### PUT /shelf/{gameId}
**Descricao:** Atualiza status de jogo na estante  
**Auth:** USER, ADMIN

**Request:**
```json
{
  "status": "FAVORITO"
}
```

**Response 200:**

**Erros:**
- `404 NOT_FOUND` - Jogo nao esta na estante

---

### DELETE /shelf/{gameId}
**Descricao:** Remove jogo da propria estante  
**Auth:** USER, ADMIN

**Response:** `204 NO_CONTENT`

**Erros:**
- `404 NOT_FOUND` - Jogo nao esta na estante

---

## 5. Threads e Comentarios (`/games/{gameId}/threads`)

> **Documentacao detalhada:** [`docs/api/discussion.md`](../api/discussion.md)

### GET /games/{gameId}/threads
**Descricao:** Lista threads de um jogo  
**Auth:** Publica  
**Paginacao:** `page`, `size`, `sort`

**Response 200:**
```json
{
  "content": [
    {
      "id": "uuid",
      "title": "Qual a melhor build para...",
      "author": { "id": "uuid", "name": "Anderson", "avatarUrl": "..." },
      "replyCount": 15,
      "likeCount": 8,
      "createdAt": "2026-05-21T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 45,
  "totalPages": 3
}
```

---

### POST /games/{gameId}/threads
**Descricao:** Abre nova thread em um jogo  
**Auth:** USER, ADMIN

**Request:**
```json
{
  "title": "Qual a melhor build para guerreiro?",
  "content": "Estou pensando em focar em forca..."
}
```

**Response 201:**
```json
{
  "id": "uuid",
  "title": "Qual a melhor build...",
  "content": "...",
  "author": { "id": "uuid", "name": "Anderson" },
  "likeCount": 0,
  "replyCount": 0,
  "createdAt": "2026-05-21T10:30:00Z"
}
```

---

### GET /games/{gameId}/threads/{threadId}
**Descricao:** Detalhe de thread com respostas paginadas  
**Auth:** Publica

**Response 200:**
```json
{
  "id": "uuid",
  "title": "Qual a melhor build...",
  "content": "...",
  "author": { "id": "uuid", "name": "Anderson", "avatarUrl": "..." },
  "likeCount": 8,
  "replies": {
    "content": [
      {
        "id": "uuid",
        "content": "Eu uso forca + destreza...",
        "author": { "id": "uuid", "name": "Outro" },
        "likeCount": 3,
        "createdAt": "2026-05-21T10:30:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 15,
    "totalPages": 1
  },
  "createdAt": "2026-05-21T10:30:00Z"
}
```

---

### POST /games/{gameId}/threads/{threadId}/replies
**Descricao:** Responde a uma thread  
**Auth:** USER, ADMIN

**Request:**
```json
{
  "content": "Eu prefiro focar em agilidade..."
}
```

**Response 201:**
```json
{
  "id": "uuid",
  "content": "...",
  "author": { "id": "uuid", "name": "Anderson" },
  "likeCount": 0,
  "createdAt": "2026-05-21T10:30:00Z"
}
```

---

### POST /games/{gameId}/threads/{threadId}/likes
**Descricao:** Dar like em uma thread  
**Auth:** USER, ADMIN  
**Idempotencia:** Sim (dar like duas vezes nao duplica)

**Response:** `204 NO_CONTENT`

---

### DELETE /games/{gameId}/threads/{threadId}/likes
**Descricao:** Remover like de uma thread  
**Auth:** USER, ADMIN

**Response:** `204 NO_CONTENT`

---

### POST /games/{gameId}/threads/{threadId}/replies/{replyId}/likes
**Descricao:** Dar like em uma resposta  
**Auth:** USER, ADMIN  
**Idempotencia:** Sim

**Response:** `204 NO_CONTENT`

---

### DELETE /games/{gameId}/threads/{threadId}/replies/{replyId}/likes
**Descricao:** Remover like de resposta  
**Auth:** USER, ADMIN

**Response:** `204 NO_CONTENT`

---

## 6. Noticias (`/news`)

> **Documentacao detalhada:** [`docs/api/news.md`](../api/news.md)

### GET /news
**Descricao:** Lista noticias paginadas  
**Auth:** Publica  
**Query params:** `gameId` (opcional, para noticias de um jogo especifico), `page`, `size`

**Response 200:**
```json
{
  "content": [
    {
      "id": "uuid",
      "title": "Witcher 4 anunciado!",
      "summary": "CD Projekt Red revela...",
      "coverImageUrl": "https://...",
      "relatedGame": { "id": "uuid", "title": "The Witcher 4" },
      "author": { "id": "uuid", "name": "Admin" },
      "publishedAt": "2026-05-21T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 200,
  "totalPages": 10
}
```

---

### GET /news/{id}
**Descricao:** Detalhe de uma noticia  
**Auth:** Publica

**Response 200:**
```json
{
  "id": "uuid",
  "title": "Witcher 4 anunciado!",
  "summary": "CD Projekt Red revela...",
  "content": "Conteudo completo da noticia...",
  "coverImageUrl": "https://...",
  "relatedGame": { "id": "uuid", "title": "The Witcher 4" },
  "author": { "id": "uuid", "name": "Admin" },
  "publishedAt": "2026-05-21T10:30:00Z"
}
```

---

### POST /news
**Descricao:** Cria nova noticia  
**Auth:** ADMIN

**Request:**
```json
{
  "title": "Witcher 4 anunciado!",
  "summary": "CD Projekt Red revela...",
  "content": "Conteudo completo...",
  "coverImageUrl": "https://...",
  "relatedGameId": "uuid"
}
```

**Response 201:**

**Erros:**
- `403 FORBIDDEN`
- `422 UNPROCESSABLE_ENTITY`

---

### PUT /news/{id}
**Auth:** ADMIN  
**Response 200:**

---

### DELETE /news/{id}
**Auth:** ADMIN  
**Response:** `204 NO_CONTENT`

---

## 7. Denuncias / Moderacao (`/admin/reports`)

> **Documentacao detalhada:** [`docs/api/moderation.md`](../api/moderation.md)

### GET /admin/reports
**Descricao:** Lista denuncias abertas  
**Auth:** ADMIN  
**Query params:** `status` (PENDING, RESOLVED, REJECTED), `page`, `size`

**Response 200:**
```json
{
  "content": [
    {
      "id": "uuid",
      "reportedUser": { "id": "uuid", "name": "Usuario X" },
      "reportedBy": { "id": "uuid", "name": "Usuario Y" },
      "reason": "ABUSIVE_BEHAVIOR",
      "description": "...",
      "status": "PENDING",
      "createdAt": "2026-05-21T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 5,
  "totalPages": 1
}
```

---

### POST /admin/reports/{id}/resolve
**Descricao:** Resolve uma denuncia (aprovando ou rejeitando)  
**Auth:** ADMIN

**Request:**
```json
{
  "decision": "APPROVED",
  "adminNote": "Usuario banido por 7 dias",
  "banDurationDays": 7
}
```

**Valores de decision:** `APPROVED` (denuncia aceita, usuario punido), `REJECTED`

**Response:** `200 OK`

**Erros:**
- `404 NOT_FOUND`
- `409 CONFLICT` - Denuncia ja resolvida
