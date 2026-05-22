# Autenticacao - API Usage

**Base URL:** `/api/v1`  
**Auth:** `PUBLICA (todos os endpoints deste grupo)`  
**Content-Type:** `application/json`

## Objetivo

Gerencia ciclo de vida de sessao: cadastro, login com emissao de JWT + refresh token opaco, renovacao de access token, logout com invalidacao e recuperacao de senha via e-mail.

---

## Endpoints

### POST /auth/register

**Descricao:** Cadastra novo usuario com role USER.  
**Idempotencia:** Nao (email unico; repetir com mesmo email retorna 409).  
**Auth:** Publica

**Requisicao:**
```json
{
  "name": "Anderson Conforto",
  "email": "anderson@email.com",
  "password": "MinhaSenh@123"
}
```

**Restricoes:**
- `name`: 2–100 caracteres, obrigatorio
- `email`: formato RFC 5322, unico no sistema
- `password`: minimo 8 chars, ao menos 1 maiuscula, 1 minuscula, 1 numero, 1 especial

**Resposta 201:**
```json
{
  "id": "a1b2c3d4-0000-0000-0000-000000000001",
  "name": "Anderson Conforto",
  "email": "anderson@email.com",
  "role": "USER",
  "createdAt": "2026-05-21T10:30:00Z"
}
```

**Erros:**
- `409 CONFLICT` - Email ja cadastrado
- `422 UNPROCESSABLE_ENTITY` - Senha fraca ou email malformado

---

### POST /auth/login

**Descricao:** Autentica usuario e emite access token JWT (HS256, 15 min) e refresh token opaco (7 dias).  
**Idempotencia:** Nao  
**Rate Limit:** 10 req/min por IP

**Requisicao:**
```json
{
  "email": "anderson@email.com",
  "password": "MinhaSenh@123"
}
```

**Resposta 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Erros:**
- `401 UNAUTHORIZED` - Credenciais invalidas
- `403 FORBIDDEN` - Usuario banido (campo `detail` inclui `bannedUntil`)
- `429 TOO_MANY_REQUESTS` - Rate limit excedido

---

### POST /auth/refresh

**Descricao:** Troca o refresh token por um novo par access + refresh. O refresh token anterior e invalidado.  
**Idempotencia:** Nao (cada chamada invalida o token anterior)  
**Auth:** Publica (refresh token no body)

**Requisicao:**
```json
{
  "refreshToken": "d290f1ee-6c54-4b01-90e6-d701748f0851"
}
```

**Resposta 200:** (mesmo formato do login)
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "c7f2b4d1-1234-5678-abcd-ef0123456789",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Erros:**
- `401 UNAUTHORIZED` - Refresh token invalido, expirado ou revogado

---

### POST /auth/logout

**Descricao:** Invalida o access token (adiciona JTI ao denylist) e revoga o refresh token. Exige autenticacao.  
**Idempotencia:** Sim  
**Auth:** USER, ADMIN  
**Headers:**
- `Authorization: Bearer <token>`

**Requisicao:** vazia

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `401 UNAUTHORIZED` - Token ausente ou invalido

---

### POST /auth/forgot-password

**Descricao:** Solicita recuperacao de senha. Envia e-mail com link de reset (valido por 1 hora, uso unico). Sempre retorna 202 independente de o email existir ou nao.  
**Idempotencia:** Sim (semanticamente; emite novo token e invalida o anterior)  
**Auth:** Publica

**Requisicao:**
```json
{
  "email": "anderson@email.com"
}
```

**Resposta:** `202 ACCEPTED`

**Erros:**
- `422 UNPROCESSABLE_ENTITY` - Email malformado

---

### POST /auth/reset-password

**Descricao:** Redefine senha usando token recebido por e-mail. Token e de uso unico e expira em 1 hora. Todos os refresh tokens do usuario sao revogados apos o reset.  
**Idempotencia:** Nao  
**Auth:** Publica

**Requisicao:**
```json
{
  "token": "f7c3b2a1-dead-beef-0000-000000000001",
  "newPassword": "NovaSenha@456"
}
```

**Resposta:** `204 NO_CONTENT`

**Erros:**
- `400 BAD_REQUEST` - Token invalido, expirado ou ja utilizado
- `422 UNPROCESSABLE_ENTITY` - Nova senha nao atende politica

---

## Cenarios de erro (detalhados)

### 1) Email ja cadastrado

**Status:** `409 CONFLICT`  
**Motivo:** Tentativa de registro com email ja existente no sistema.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/duplicate-entity",
  "title": "Conflict",
  "status": 409,
  "detail": "Email 'anderson@email.com' ja esta em uso",
  "instance": "/api/v1/auth/register",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "abc123"
}
```

### 2) Senha fraca no registro

**Status:** `422 UNPROCESSABLE_ENTITY`  
**Motivo:** Senha nao atende politica (minimo 8 chars, 1 maiuscula, 1 minuscula, 1 numero, 1 especial).  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/validation-error",
  "title": "Validation Error",
  "status": 422,
  "detail": "A senha deve ter ao menos 8 caracteres, 1 maiuscula, 1 minuscula, 1 numero e 1 caractere especial",
  "instance": "/api/v1/auth/register",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "def456"
}
```

### 3) Refresh token invalido ou revogado

**Status:** `401 UNAUTHORIZED`  
**Motivo:** Token opaco nao encontrado na base, expirado ou ja revogado (logout previo ou reset de senha).  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Refresh token invalido ou expirado",
  "instance": "/api/v1/auth/refresh",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "ghi789"
}
```

### 4) Usuario banido no login

**Status:** `403 FORBIDDEN`  
**Motivo:** Usuario possui `bannedUntil` no futuro.  
**Exemplo de resposta (RFC 7807):**
```json
{
  "type": "https://blogames.com/errors/forbidden",
  "title": "Forbidden",
  "status": 403,
  "detail": "Usuario banido ate 2026-05-28T10:30:00Z",
  "instance": "/api/v1/auth/login",
  "timestamp": "2026-05-21T10:30:00Z",
  "traceId": "jkl012"
}
```

---

## Notas

- Access token: JWT HS256, payload inclui `sub` (userId), `roles`, `jti` (para denylist), `exp` (15 min).
- Refresh token: opaco (UUID), armazenado hasheado no banco, expira em 7 dias.
- Denylist de JTI limpa tokens expirados periodicamente (coluna `expiresAt`).
- O endpoint `/auth/logout` nao precisa de refresh token no body — invalida o JTI do access token do header e busca o refresh ativo do usuario para revogar.
- Reset de senha revoga todos os refresh tokens do usuario como medida de seguranca.
