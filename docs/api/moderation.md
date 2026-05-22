# Moderacao (Denuncias e Bans) - API Usage

**Base URL:** `/api/v1`  
**Auth:** `USER ou ADMIN (criar denuncia) | ADMIN (listar e resolver denuncias, banir)`  
**Content-Type:** `application/json`

## Objetivo

Gerencia o ciclo de moderacao da plataforma: usuarios podem denunciar outros usuarios; admins visualizam as denuncias abertas, aprovam (com ban opcional) ou rejeitam. Bans definem uma data de expiracao a partir da qual o usuario pode voltar a autenticar.

---

## Endpoints

### POST /users/{id}/report

**Descricao:** Denuncia um usuario por comportamento inadequado. Um usuario nao pode ter multiplas denuncias PENDING do mesmo denunciante para o mesmo denunciado.  
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
- `401 UNAUTHORIZED`
- `404 NOT_FOUND` - Usuario denunciado nao encontrado
- `409 CONFLICT` - Denuncia PENDING ja existe do mesmo denunciante para o mesmo denunciado
- `422 UNPROCESSABLE_ENTITY` - Reason invalido ou description muito longa

---

### GET /admin/reports

**Descricao:** Lista denuncias com filtro opcional por status. Uso exclusivo de admins.  
**Idempotencia:** Sim  
**Auth:** ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Parametros de query:**
- `status`: `PENDING`, `APPROVED`, `REJECTED` (opcional; sem filtro retorna todas)
- `page`: pagina (default: `0`)
- `size`: tamanho da pagina (default: `20`)

**Resposta 200:**
```json
{
  "content": [
    {
      "id": "r1a2b3c4-0000-0000-0000-000000000001",
      "reportedUser": {
        "id": "u1000000-0000-0000-0000-000000000001",
        "name": "Usuario X"
      },
      "reportedBy": {
        "id": "u2000000-0000-0000-0000-000000000001",
        "name": "Usuario Y"
      },
      "reason": "ABUSIVE_BEHAVIOR",
      "description": "Usuario enviou mensagens ofensivas...",
      "status": "PENDING",
      "adminNote": null,
      "resolvedBy": null,
      "resolvedAt": null,
      "createdAt": "2026-05-21T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 5,
  "totalPages": 1
}
```

**Erros:**
- `401 UNAUTHORIZED`
- `403 FORBIDDEN` - Nao e ADMIN

---

### POST /admin/reports/{id}/resolve

**Descricao:** Resolve uma denuncia aprovando (com ban opcional do denunciado) ou rejeitando. So pode ser chamado em denuncias com status PENDING.  
**Idempotencia:** Nao  
**Auth:** ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Path params:**
- `id`: UUID da denuncia

**Requisicao:**
```json
{
  "decision": "APPROVED",
  "adminNote": "Usuario banido por comportamento abusivo confirmado",
  "banDurationDays": 7
}
```

**Campos:**
- `decision`: `APPROVED` (denuncia aceita) ou `REJECTED` (denuncia rejeitada) — obrigatorio
- `adminNote`: nota interna do admin — opcional, max 2000 chars
- `banDurationDays`: dias de ban a aplicar ao denunciado (apenas quando `decision=APPROVED`) — opcional; se omitido com APPROVED, denuncia e aprovada sem ban imediato

**Resposta 200:**
```json
{
  "id": "r1a2b3c4-0000-0000-0000-000000000001",
  "status": "APPROVED",
  "adminNote": "Usuario banido por comportamento abusivo confirmado",
  "resolvedBy": {
    "id": "a0000000-0000-0000-0000-000000000001",
    "name": "Admin"
  },
  "resolvedAt": "2026-05-21T12:00:00Z",
  "bannedUntil": "2026-05-28T12:00:00Z"
}
```

**Erros:**
- `401 UNAUTHORIZED`
- `403 FORBIDDEN` - Nao e ADMIN
- `404 NOT_FOUND` - Denuncia nao encontrada
- `409 CONFLICT` - Denuncia ja foi resolvida (status != PENDING)

---

## Cenarios de erro (detalhados)

### 1) Tentativa de denunciar a si mesmo

**Status:** `400 BAD_REQUEST`  
**Motivo:** O `id` do path e igual ao `userId` extraido do token autenticado.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/self-reference",
  "title": "Bad Request",
  "status": 400,
  "detail": "Um usuario nao pode denunciar a si mesmo",
  "instance": "/api/v1/users/uuid/report",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "abc123"
}
```

### 2) Denuncia PENDING ja existente

**Status:** `409 CONFLICT`  
**Motivo:** Ja existe uma denuncia com `status=PENDING` do mesmo denunciante (`reportedById`) para o mesmo denunciado.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/duplicate-entity",
  "title": "Conflict",
  "status": 409,
  "detail": "Voce ja possui uma denuncia pendente para este usuario",
  "instance": "/api/v1/users/uuid/report",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "def456"
}
```

### 3) Denuncia ja resolvida

**Status:** `409 CONFLICT`  
**Motivo:** Tentativa de resolver uma denuncia cujo status ja e `APPROVED` ou `REJECTED`.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/duplicate-entity",
  "title": "Conflict",
  "status": 409,
  "detail": "A denuncia 'uuid' ja foi resolvida com status APPROVED",
  "instance": "/api/v1/admin/reports/uuid/resolve",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "ghi789"
}
```

### 4) Reason invalido

**Status:** `422 UNPROCESSABLE_ENTITY`  
**Motivo:** O valor de `reason` nao e um dos valores permitidos do enum `ReportReason`.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/validation-error",
  "title": "Validation Error",
  "status": 422,
  "detail": "Reason invalido: 'UNKNOWN_REASON'. Valores aceitos: ABUSIVE_BEHAVIOR, SPAM, HARASSMENT, INAPPROPRIATE_CONTENT, OTHER",
  "instance": "/api/v1/users/uuid/report",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "jkl012"
}
```

---

## Notas

- Ban e aplicado no campo `bannedUntil` do agregado `User`; durante o login, a aplicacao verifica `bannedUntil > now()` e retorna `403`.
- `banDurationDays` e relativo ao momento da resolucao (`resolvedAt + N dias`).
- Se `decision=REJECTED`, `banDurationDays` e ignorado mesmo que informado.
- Denuncias resolvidas nao podem ser reabertas; crie uma nova denuncia se necessario.
- `resolvedBy` e sempre o admin autenticado que chamou o endpoint (extraido do JWT); nao informado no body.
- O endpoint `POST /users/{id}/report` esta documentado tambem em [`docs/api/users.md`](./users.md) por ser rota de usuario.
