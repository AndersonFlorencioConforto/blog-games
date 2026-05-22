# Fronteiras Transacionais - Blog Games Platform

**Versao:** 1.0  
**Data:** 2026-05-21

---

## 1. Principios

- `@Transactional` e aplicado exclusivamente na camada `application` (casos de uso)
- Controllers NUNCA anotados com `@Transactional`
- Domain nao conhece transacoes; invariantes sao verificadas antes do save
- Repositorios podem ter `@Transactional(readOnly = true)` em metodos de leitura
- Eventos de dominio sao publicados APOS o commit (`AFTER_COMMIT`) quando o handler tem efeito colateral externo (e-mail, revogacao de tokens)
- Eventos publicados DENTRO da transacao (antes do commit) quando o handler faz parte da mesma unidade de consistencia (ex: atualizar media de avaliacao)

---

## 2. Tabela de Fronteiras por Caso de Uso

### Autenticacao e Usuarios

| Caso de Uso | Propagacao | ReadOnly | O que acontece na transacao | Rollback em |
|---|---|---|---|---|
| `RegisterUserUseCase` | REQUIRED | false | INSERT users | Duplicidade de email, validacao de dominio |
| `LoginUseCase` | REQUIRED | false | SELECT users, INSERT refresh_tokens | Qualquer excecao |
| `LogoutUseCase` | REQUIRED | false | INSERT token_blocklist, UPDATE refresh_tokens.revoked_at | Qualquer excecao |
| `RefreshTokenUseCase` | REQUIRED | false | UPDATE refresh_tokens (revoke), INSERT refresh_tokens (novo) | Token invalido, expirado |
| `RequestPasswordResetUseCase` | REQUIRED | false | SELECT users, INSERT password_reset_tokens | Qualquer excecao |
| `ResetPasswordUseCase` | REQUIRED | false | UPDATE users.password_hash, UPDATE password_reset_tokens.used_at, DELETE refresh_tokens | Token invalido, ja usado |
| `GetUserProfileUseCase` | SUPPORTS | true | SELECT users | Nao aplicavel (somente leitura) |
| `UpdateUserProfileUseCase` | REQUIRED | false | UPDATE users | Validacao de dominio |
| `FollowUserUseCase` | REQUIRED | false | INSERT follows | Auto-follow, usuario nao encontrado |
| `UnfollowUserUseCase` | REQUIRED | false | DELETE follows | Nao aplicavel (idempotente) |

### Jogos

| Caso de Uso | Propagacao | ReadOnly | O que acontece na transacao | Rollback em |
|---|---|---|---|---|
| `CreateGameUseCase` | REQUIRED | false | INSERT games, INSERT game_categories, INSERT game_platforms | Titulo duplicado, validacao |
| `UpdateGameUseCase` | REQUIRED | false | UPDATE games, DELETE+INSERT categories/platforms | Jogo nao encontrado |
| `DeleteGameUseCase` | REQUIRED | false | UPDATE games.deleted_at (soft delete) | Jogo nao encontrado |
| `ListGamesUseCase` | SUPPORTS | true | SELECT games com filtros e paginacao | Nao aplicavel |
| `GetGameDetailUseCase` | SUPPORTS | true | SELECT game + categories + platforms | Nao aplicavel |
| `RateGameUseCase` | REQUIRED | false | UPSERT ratings, UPDATE games.average_user_rating e total_ratings | Stars invalido, jogo nao encontrado |

### Estante

| Caso de Uso | Propagacao | ReadOnly | O que acontece na transacao | Rollback em |
|---|---|---|---|---|
| `AddToShelfUseCase` | REQUIRED | false | INSERT shelf_items | Jogo nao encontrado, jogo ja na estante |
| `UpdateShelfItemUseCase` | REQUIRED | false | UPDATE shelf_items.status | Item nao encontrado |
| `RemoveFromShelfUseCase` | REQUIRED | false | DELETE shelf_items | Nao aplicavel (idempotente) |
| `GetUserShelfUseCase` | SUPPORTS | true | SELECT shelf_items JOIN games | Nao aplicavel |

### Threads e Respostas

| Caso de Uso | Propagacao | ReadOnly | O que acontece na transacao | Rollback em |
|---|---|---|---|---|
| `CreateThreadUseCase` | REQUIRED | false | INSERT threads | Jogo nao encontrado, validacao |
| `ReplyToThreadUseCase` | REQUIRED | false | INSERT replies, UPDATE threads.reply_count | Thread nao encontrada |
| `LikeThreadUseCase` | REQUIRED | false | INSERT thread_likes (se nao existir), UPDATE threads.like_count | Qualquer excecao |
| `UnlikeThreadUseCase` | REQUIRED | false | DELETE thread_likes, UPDATE threads.like_count | Nao aplicavel |
| `LikeReplyUseCase` | REQUIRED | false | INSERT reply_likes, UPDATE replies.like_count | Qualquer excecao |
| `UnlikeReplyUseCase` | REQUIRED | false | DELETE reply_likes, UPDATE replies.like_count | Nao aplicavel |
| `ListThreadsUseCase` | SUPPORTS | true | SELECT threads paginado | Nao aplicavel |
| `GetThreadDetailUseCase` | SUPPORTS | true | SELECT thread + replies paginado | Nao aplicavel |

### Noticias

| Caso de Uso | Propagacao | ReadOnly | O que acontece na transacao | Rollback em |
|---|---|---|---|---|
| `CreateNewsUseCase` | REQUIRED | false | INSERT news | Validacao |
| `UpdateNewsUseCase` | REQUIRED | false | UPDATE news | Noticia nao encontrada |
| `DeleteNewsUseCase` | REQUIRED | false | DELETE news | Noticia nao encontrada |
| `ListNewsUseCase` | SUPPORTS | true | SELECT news paginado | Nao aplicavel |

### Moderacao

| Caso de Uso | Propagacao | ReadOnly | O que acontece na transacao | Rollback em |
|---|---|---|---|---|
| `ReportUserUseCase` | REQUIRED | false | INSERT reports | Auto-report, usuario nao encontrado |
| `ListReportsUseCase` | SUPPORTS | true | SELECT reports paginado | Nao aplicavel |
| `ResolveReportUseCase` | REQUIRED | false | UPDATE reports.status, UPDATE users.banned_until (se aprovado), DELETE refresh_tokens (se banido) | Report nao encontrado, ja resolvido |

---

## 3. Casos de Uso Criticos com Detalhe

### RateGameUseCase - Atualizacao Atomica da Media

```
TRANSACAO UNICA:
  1. SELECT rating WHERE game_id = ? AND user_id = ? (verificar se existe)
  2a. Se nao existe: INSERT rating; UPDATE games SET average_user_rating = ..., total_ratings = total_ratings + 1
  2b. Se existe: UPDATE rating SET stars = ?; UPDATE games SET average_user_rating = ... (recalculo)
  3. COMMIT

  APOS COMMIT (AFTER_COMMIT):
  - Publicar GameRatedEvent (opcional, sem handlers criticos no Release 1)
```

**Risco:** Atualizacao concorrente de `average_user_rating`. Mitigacao: `SELECT FOR UPDATE` na linha do jogo durante a atualizacao da media, ou aceitar eventual inconsistencia temporaria com job de recalculo periodico.

**Decisao:** Para Release 1, usar `SELECT FOR UPDATE` via JPA pessimista (`@Lock(PESSIMISTIC_WRITE)`) ao buscar o Game para atualizar a media. Revisar se causar contenao em producao.

---

### ResolveReportUseCase - Transacao Multi-Entidade

```
TRANSACAO UNICA:
  1. SELECT report WHERE id = ? FOR UPDATE (evitar resolucao dupla)
  2. Verificar status = PENDING (senao, lancar ReportAlreadyResolvedException -> 409)
  3. UPDATE report SET status = APPROVED/REJECTED, resolved_by = ?, resolved_at = NOW()
  4. Se APPROVED E banDurationDays > 0:
     4a. UPDATE users SET banned_until = NOW() + interval
     4b. UPDATE refresh_tokens SET revoked_at = NOW() WHERE user_id = ? AND revoked_at IS NULL
  5. COMMIT

  APOS COMMIT (AFTER_COMMIT):
  - Publicar UserBannedEvent -> RevokeUserTokensHandler
    (insere JTIs dos access tokens ativos na denylist - consulta separada)
```

**Problema do handler AFTER_COMMIT:** Access tokens ativos nao estao armazenados no banco (sao JWT stateless). O handler de revogacao apenas revoga refresh tokens (step 4b) e a denylist e preenchida quando o token banido tentar autenticar (verificacao no filtro detectara `bannedUntil`). Esta e a janela aceitavel de inconsistencia: ate expirar o access token (max 15min), o usuario banido pode fazer requisicoes. Janela documentada e aceita.

---

### LogoutUseCase - Garantia de Invalidacao

```
TRANSACAO UNICA:
  1. Extrair jti do access token atual (ja validado no filtro)
  2. INSERT token_blocklist (jti, expires_at = jwt.expiration)
  3. UPDATE refresh_tokens SET revoked_at = NOW() WHERE token_hash = ? (refresh token enviado no body ou o mais recente do usuario)
  4. COMMIT
```

**Rollback:** Se INSERT na blocklist falhar, o refresh token nao e revogado (transacao faz rollback completo). Usuario deve tentar logout novamente.

---

## 4. Configuracao JPA Recomendada

```properties
# Evitar N+1 queries
spring.jpa.open-in-view=false

# Batch inserts para colecoes (categories, platforms)
spring.jpa.properties.hibernate.jdbc.batch_size=10
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

**`open-in-view=false` e obrigatorio:** Evita que o contexto de persistencia fique aberto durante a serializacao HTTP (anti-pattern que mascara N+1 e causa problemas em producao).

---

## 5. Regras de Rollback

- Excecoes de dominio (`DomainException` e subclasses) causam rollback automatico (sao `RuntimeException`)
- Excecoes verificadas (checked) NAO causam rollback por padrao; usar `@Transactional(rollbackFor = Exception.class)` se necessario
- Falha de envio de e-mail NAO deve causar rollback da transacao principal de reset de senha (o token ja esta salvo; usuario pode solicitar novo e-mail)
- Solucao: `SendPasswordResetEmailHandler` e chamado `AFTER_COMMIT`, fora da transacao principal

---

## 6. Compensacoes (sem Saga formal)

O sistema nao usa Saga pattern (nao ha microservicos). Para operacoes que falham parcialmente:

| Cenario | Compensacao |
|---|---|
| Token inserido na blocklist mas refresh nao revogado | Job de limpeza; impacto baixo (refresh expira em 7 dias) |
| Media de avaliacao inconsistente | Job semanal de recalculo via `SELECT AVG` |
| Reply inserida mas reply_count nao incrementado | Job de recalculo de contadores (a implementar se necessario) |
