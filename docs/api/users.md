# Usuarios (Perfil e Social) - API Usage

**Base URL:** `/api/v1`  
**Auth:** `PUBLICA (leitura) | USER ou ADMIN (escrita, follow, report)`  
**Content-Type:** `application/json`

## Objetivo

Gerencia perfis de usuarios (visualizacao propria e de terceiros, edicao), relacoes sociais (seguir/deixar de seguir, listar seguidores/seguindo) e denuncia de usuarios.

---

## Endpoints

### GET /users/me

**Descricao:** Retorna dados completos do usuario autenticado, incluindo email e role.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Resposta 200:**
```json
{
  "id": "a1b2c3d4-0000-0000-0000-000000000001",
  "name": "Anderson Conforto",
  "email": "anderson@email.com",
  "bio": "Gamer desde 1995",
  "avatarUrl": "https://cdn.example.com/avatars/uuid.jpg",
  "role": "USER",
  "followersCount": 42,
  "followingCount": 18,
  "createdAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido

---

### PUT /users/me

**Descricao:** Atualiza nome, bio e avatarUrl do usuario autenticado.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Requisicao:**
```json
{
  "name": "Anderson Conforto",
  "bio": "Gamer desde 1995",
  "avatarUrl": "https://cdn.example.com/avatars/uuid.jpg"
}
```

**Restricoes:**
- `name`: 2–100 caracteres, obrigatorio
- `bio`: max 500 caracteres, opcional
- `avatarUrl`: URL valida, opcional

**Resposta 200:** (perfil publico atualizado)
```json
{
  "id": "a1b2c3d4-0000-0000-0000-000000000001",
  "name": "Anderson Conforto",
  "bio": "Gamer desde 1995",
  "avatarUrl": "https://cdn.example.com/avatars/uuid.jpg",
  "followersCount": 42,
  "followingCount": 18,
  "createdAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `422 UNPROCESSABLE_ENTITY` - Dados invalidos (nome muito curto, bio muito longa)

---

### GET /users/{id}

**Descricao:** Exibe perfil publico de qualquer usuario.  
**Idempotencia:** Sim  
**Auth:** Publica

**Path params:**
- `id`: UUID do usuario

**Resposta 200:**
```json
{
  "id": "a1b2c3d4-0000-0000-0000-000000000001",
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

### POST /users/{id}/follow

**Descricao:** Seguir um usuario. Idempotente — seguir quem ja se segue nao gera erro.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID do usuario a seguir

**Requisicao:** vazia

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `400 BAD_REQUEST` - Tentativa de seguir a si mesmo
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Usuario nao encontrado

---

### DELETE /users/{id}/follow

**Descricao:** Deixar de seguir um usuario. Idempotente — deixar de seguir quem nao se segue e no-op.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID do usuario a deixar de seguir

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Usuario nao encontrado

---

### GET /users/{id}/followers

**Descricao:** Lista seguidores de um usuario, paginados.  
**Idempotencia:** Sim  
**Auth:** Publica

**Path params:**
- `id`: UUID do usuario

**Parametros de query:**
- `page`: pagina (default: `0`)
- `size`: tamanho da pagina (default: `20`)

**Resposta 200:**
```json
{
  "content": [
    {
      "id": "b2c3d4e5-0000-0000-0000-000000000001",
      "name": "Outro Gamer",
      "avatarUrl": "https://cdn.example.com/avatars/outro.jpg"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 42,
  "totalPages": 3
}
```

**Erros:**
- `404 NOT_FOUND` - Usuario nao encontrado

---

### GET /users/{id}/following

**Descricao:** Lista usuarios que um usuario segue, paginados.  
**Idempotencia:** Sim  
**Auth:** Publica

**Path params:**
- `id`: UUID do usuario

**Parametros de query:**
- `page`: pagina (default: `0`)
- `size`: tamanho da pagina (default: `20`)

**Resposta 200:** (mesmo formato de /followers)

**Erros:**
- `404 NOT_FOUND` - Usuario nao encontrado

---

### POST /users/{id}/report

**Descricao:** Denuncia um usuario por comportamento inadequado. Um usuario nao pode ter multiplas denuncias PENDING do mesmo denunciante.  
**Idempotencia:** Nao  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID do usuario a denunciar

**Requisicao:**
```json
{
  "reason": "ABUSIVE_BEHAVIOR",
  "description": "Usuario enviou mensagens ofensivas na thread do jogo X"
}
```

**Valores validos para `reason`:** `ABUSIVE_BEHAVIOR`, `SPAM`, `HARASSMENT`, `INAPPROPRIATE_CONTENT`, `OTHER`

**Restricoes:**
- `reason`: obrigatorio
- `description`: opcional, max 1000 caracteres

**Resposta:** `201 CREATED`

**Erros:**
- `400 BAD_REQUEST` - Tentativa de denunciar a si mesmo
- `401 UNAUTHORIZED` - Token ausente ou invalido
- `404 NOT_FOUND` - Usuario denunciado nao encontrado
- `409 CONFLICT` - Denuncia PENDING ja existente do mesmo denunciante para o mesmo denunciado
- `422 UNPROCESSABLE_ENTITY` - Reason invalido ou description muito longa

---

## Cenarios de erro (detalhados)

### 1) Tentativa de seguir a si mesmo

**Status:** `400 BAD_REQUEST`  
**Motivo:** O `id` do path e igual ao `userId` do token autenticado.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/self-reference",
  "title": "Bad Request",
  "status": 400,
  "detail": "Um usuario nao pode seguir a si mesmo",
  "instance": "/api/v1/users/uuid/follow",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "abc123"
}
```

### 2) Dados invalidos ao atualizar perfil

**Status:** `422 UNPROCESSABLE_ENTITY`  
**Motivo:** Campo `name` fora do range permitido.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/validation-error",
  "title": "Validation Error",
  "status": 422,
  "detail": "O campo 'name' deve ter entre 2 e 100 caracteres",
  "instance": "/api/v1/users/me",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "def456"
}
```

### 3) Denuncia duplicada PENDING

**Status:** `409 CONFLICT`  
**Motivo:** Ja existe uma denuncia com status PENDING do mesmo denunciante para o mesmo usuario.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/duplicate-entity",
  "title": "Conflict",
  "status": 409,
  "detail": "Voce ja possui uma denuncia pendente para este usuario",
  "instance": "/api/v1/users/uuid/report",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "ghi789"
}
```

---

## Notas

- `followersCount` e `followingCount` sao calculados via COUNT na tabela `follows` a cada leitura (nao desnormalizados).
- Follow e idempotente por design: a implementacao verifica existencia antes de inserir.
- O endpoint `POST /users/{id}/report` e implementado no slice de Moderacao; ver [`docs/api/moderation.md`](./moderation.md) para o fluxo de resolucao de denuncias.
