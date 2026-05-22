# Matriz de Seguranca - Blog Games Platform

**Versao:** 1.0  
**Data:** 2026-05-21

---

## 1. Roles

| Role | Descricao |
|---|---|
| `ADMIN` | Gerencia catalogo de jogos, publica noticias, modera denuncias, pode banir usuarios |
| `USER` | Usuario autenticado com acesso a funcionalidades da plataforma |
| `ANONIMO` | Nao autenticado; acesso apenas a rotas publicas de leitura |

---

## 2. Matriz de Acesso por Endpoint

### Autenticacao

| Endpoint | Metodo | ANONIMO | USER | ADMIN | Observacoes |
|---|---|---|---|---|---|
| `/auth/register` | POST | PERMITIDO | - | - | Publica |
| `/auth/login` | POST | PERMITIDO | - | - | Rate limit 10/min por IP |
| `/auth/refresh` | POST | PERMITIDO | - | - | Requer refresh token valido |
| `/auth/logout` | POST | NEGADO | PERMITIDO | PERMITIDO | Invalida tokens |
| `/auth/forgot-password` | POST | PERMITIDO | - | - | Sempre retorna 202 |
| `/auth/reset-password` | POST | PERMITIDO | - | - | Token de uso unico |

### Usuarios

| Endpoint | Metodo | ANONIMO | USER | ADMIN | Observacoes |
|---|---|---|---|---|---|
| `/users/{id}` | GET | PERMITIDO | PERMITIDO | PERMITIDO | Perfil publico |
| `/users/me` | GET | NEGADO | PERMITIDO | PERMITIDO | Dados sensiveis proprios |
| `/users/me` | PUT | NEGADO | PERMITIDO | PERMITIDO | Edita apenas proprio perfil |
| `/users/{id}/follow` | POST | NEGADO | PERMITIDO | PERMITIDO | Nao pode seguir a si mesmo |
| `/users/{id}/follow` | DELETE | NEGADO | PERMITIDO | PERMITIDO | |
| `/users/{id}/followers` | GET | PERMITIDO | PERMITIDO | PERMITIDO | |
| `/users/{id}/following` | GET | PERMITIDO | PERMITIDO | PERMITIDO | |
| `/users/{id}/report` | POST | NEGADO | PERMITIDO | PERMITIDO | Nao pode denunciar a si mesmo |

### Jogos

| Endpoint | Metodo | ANONIMO | USER | ADMIN | Observacoes |
|---|---|---|---|---|---|
| `/games` | GET | PERMITIDO | PERMITIDO | PERMITIDO | Listagem publica |
| `/games/{id}` | GET | PERMITIDO | PERMITIDO | PERMITIDO | Detalhe publico |
| `/games` | POST | NEGADO | NEGADO | PERMITIDO | Somente ADMIN |
| `/games/{id}` | PUT | NEGADO | NEGADO | PERMITIDO | Somente ADMIN |
| `/games/{id}` | DELETE | NEGADO | NEGADO | PERMITIDO | Somente ADMIN |
| `/games/{id}/ratings` | POST | NEGADO | PERMITIDO | PERMITIDO | Avaliar jogo |
| `/games/{id}/ratings/me` | GET | NEGADO | PERMITIDO | PERMITIDO | Ver propria avaliacao |

### Estante

| Endpoint | Metodo | ANONIMO | USER | ADMIN | Observacoes |
|---|---|---|---|---|---|
| `/users/{id}/shelf` | GET | PERMITIDO | PERMITIDO | PERMITIDO | Estante publica |
| `/shelf` | POST | NEGADO | PERMITIDO | PERMITIDO | Adiciona a propria estante |
| `/shelf/{gameId}` | PUT | NEGADO | PERMITIDO | PERMITIDO | Atualiza propria estante |
| `/shelf/{gameId}` | DELETE | NEGADO | PERMITIDO | PERMITIDO | Remove da propria estante |

### Threads e Respostas

| Endpoint | Metodo | ANONIMO | USER | ADMIN | Observacoes |
|---|---|---|---|---|---|
| `/games/{id}/threads` | GET | PERMITIDO | PERMITIDO | PERMITIDO | |
| `/games/{id}/threads` | POST | NEGADO | PERMITIDO | PERMITIDO | |
| `/games/{id}/threads/{tId}` | GET | PERMITIDO | PERMITIDO | PERMITIDO | |
| `/games/{id}/threads/{tId}/replies` | POST | NEGADO | PERMITIDO | PERMITIDO | |
| `/games/{id}/threads/{tId}/likes` | POST | NEGADO | PERMITIDO | PERMITIDO | |
| `/games/{id}/threads/{tId}/likes` | DELETE | NEGADO | PERMITIDO | PERMITIDO | |
| `/games/{id}/threads/{tId}/replies/{rId}/likes` | POST | NEGADO | PERMITIDO | PERMITIDO | |
| `/games/{id}/threads/{tId}/replies/{rId}/likes` | DELETE | NEGADO | PERMITIDO | PERMITIDO | |

### Noticias

| Endpoint | Metodo | ANONIMO | USER | ADMIN | Observacoes |
|---|---|---|---|---|---|
| `/news` | GET | PERMITIDO | PERMITIDO | PERMITIDO | |
| `/news/{id}` | GET | PERMITIDO | PERMITIDO | PERMITIDO | |
| `/news` | POST | NEGADO | NEGADO | PERMITIDO | |
| `/news/{id}` | PUT | NEGADO | NEGADO | PERMITIDO | |
| `/news/{id}` | DELETE | NEGADO | NEGADO | PERMITIDO | |

### Moderacao (Admin)

| Endpoint | Metodo | ANONIMO | USER | ADMIN | Observacoes |
|---|---|---|---|---|---|
| `/admin/reports` | GET | NEGADO | NEGADO | PERMITIDO | |
| `/admin/reports/{id}/resolve` | POST | NEGADO | NEGADO | PERMITIDO | |

---

## 3. Politicas de Seguranca por Camada

### Camada de Transporte
- HTTPS obrigatorio em `dev`, `staging` e `production`
- HTTP aceito somente em `local`
- HSTS header em producao: `Strict-Transport-Security: max-age=31536000`

### Camada de Autenticacao (Spring Security Filter Chain)
- `JwtAuthenticationFilter` extrai e valida token antes de cada requisicao
- Verificacoes em ordem:
  1. Token presente no header `Authorization: Bearer <token>`
  2. Assinatura JWT valida (HS256)
  3. Token nao expirado
  4. JTI nao presente na `token_blocklist`
  5. Usuario existe e `active = true`
  6. Usuario nao banido (`bannedUntil IS NULL OR bannedUntil < NOW()`)

### Camada de Autorizacao
- `@PreAuthorize("hasRole('ADMIN')")` em endpoints de gerenciamento
- Verificacao de propriedade nos casos de uso: usuario so pode editar seus proprios dados
  - Exemplo: `PUT /users/me` verifica que `authenticatedUserId == targetUserId`
  - Exemplo: `DELETE /shelf/{gameId}` verifica que o item pertence ao usuario autenticado

### Seguranca de Senhas
- BCrypt com fator de custo 12
- Politica minima: 8 chars, 1 maiuscula, 1 minuscula, 1 numero, 1 caracter especial
- Nenhum log de senha em nenhuma camada

### Rate Limiting
- Endpoint `/auth/login`: max 10 tentativas por IP por minuto
- Endpoint `/auth/forgot-password`: max 3 tentativas por IP por hora
- Implementacao: Bucket4j (biblioteca Java, sem dependencia de Redis no escopo atual)
- Armazenamento de contadores: em memoria (JVM); reset em restart do servidor
- **Premissa P-015:** Para producao com multiplas instancias, migrar contadores do Bucket4j para Redis. Adicionar ADR especifico ao escalar.

### Headers de Seguranca (Spring Security Defaults + Customizacoes)
```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Cache-Control: no-cache, no-store, must-revalidate
Pragma: no-cache
```

### CORS
- `local` e `dev`: permitir `localhost:*`
- `staging` e `production`: apenas origens explicitamente configuradas via variavel de ambiente `ALLOWED_ORIGINS`

---

## 4. Auditoria

Campos de auditoria presentes em todas as tabelas:
- `created_at` - timestamp de criacao
- `updated_at` - timestamp de ultima modificacao

Auditoria de acoes sensiveis (log estruturado):
- Login bem-sucedido: `userId`, `ip`, `timestamp`
- Tentativa de login falha: `email`, `ip`, `timestamp`
- Logout: `userId`, `jti`, `timestamp`
- Ban de usuario: `targetUserId`, `adminId`, `bannedUntil`, `timestamp`
- Resolucao de denuncia: `reportId`, `adminId`, `decision`, `timestamp`

---

## 5. Variaveis de Ambiente Sensiveis

| Variavel | Descricao | Obrigatoria |
|---|---|---|
| `JWT_SECRET` | Chave simetrica HS256 (minimo 32 bytes) | Sim |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | Conexao PostgreSQL | Sim (nao-local) |
| `DB_USER`, `DB_PASSWORD` | Credenciais DB | Sim (nao-local) |
| `SMTP_HOST`, `SMTP_PORT` | Servidor de e-mail | Sim |
| `SMTP_USERNAME`, `SMTP_PASSWORD` | Credenciais SMTP | Sim |
| `ALLOWED_ORIGINS` | Origens CORS permitidas | Sim (staging/production) |
| `APP_BASE_URL` | URL base para links em e-mails | Sim |

Nenhuma dessas variaveis deve ser comitada em arquivos de configuracao versionados. Usar `.env` local (gitignored) ou secrets manager em producao.
