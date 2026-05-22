# Catalogo de Dominio - Blog Games Platform

**Versao:** 1.0  
**Data:** 2026-05-21

---

## 1. Visao Geral dos Agregados

```
Bounded Contexts identificados:
  - Identity (Usuario, autenticacao, roles)
  - Catalog (Jogo, categoria, plataforma)
  - Rating (Avaliacao de jogo por usuario)
  - Shelf (Estante do usuario)
  - Social (Seguir/ser seguido)
  - Discussion (Thread, Reply, Like)
  - News (Noticia editorial)
  - Moderation (Denuncia, Ban)
```

Para o escopo monolitico atual, todos os bounded contexts vivem no mesmo modulo `domain`, organizados em pacotes separados.

---

## 2. Entidades e Agregados

### 2.1 Agregado: User

**Raiz do Agregado:** `User`  
**Pacote:** `br.com.andersondev.domain.user`

**Entidade User:**

| Campo | Tipo | Restricoes |
|---|---|---|
| id | UserId (UUID) | Imutavel, gerado na criacao |
| name | String | Nao nulo, 2-100 chars |
| email | Email (VO) | Unico, nao nulo, formato valido |
| passwordHash | String | Nao nulo, armazenado como hash BCrypt |
| role | Role (enum) | USER ou ADMIN; default USER |
| bio | String | Opcional, max 500 chars |
| avatarUrl | String | Opcional, URL valida |
| bannedUntil | Instant | Nullable; usuario banido se now() < bannedUntil |
| active | boolean | true por default; soft-delete opcional |
| createdAt | Instant | Imutavel |
| updatedAt | Instant | Atualizado a cada modificacao |

**Invariantes:**
- Email deve ser unico no sistema
- Senha deve ter minimo 8 chars, ao menos 1 maiuscula, 1 minuscula, 1 numero, 1 especial
- Usuario banido nao pode autenticar ate `bannedUntil`
- Usuario nao pode seguir a si mesmo
- Usuario nao pode denunciar a si mesmo

**Eventos de dominio:**
- `UserRegisteredEvent` - emitido apos cadastro bem-sucedido
- `UserBannedEvent` - emitido apos ban pelo admin
- `PasswordResetRequestedEvent` - emitido apos solicitacao de recuperacao

**Value Objects internos:**
- `UserId` - wrapper UUID imutavel
- `Email` - valida formato RFC 5322
- `Role` - enum: USER, ADMIN

---

### 2.2 Agregado: Game

**Raiz do Agregado:** `Game`  
**Pacote:** `br.com.andersondev.domain.game`

**Entidade Game:**

| Campo | Tipo | Restricoes |
|---|---|---|
| id | GameId (UUID) | Imutavel |
| title | String | Nao nulo, 1-200 chars, unico |
| description | String | Nao nulo, 1-5000 chars |
| editorialReview | String | Opcional, max 10000 chars |
| platformScore | BigDecimal | 0.0 a 10.0, duas casas decimais |
| pros | List<String> | Opcional, max 10 itens, cada um max 200 chars |
| cons | List<String> | Opcional, max 10 itens, cada um max 200 chars |
| coverImageUrl | String | Opcional, URL valida |
| categories | Set<Category> | Ao menos 1 categoria |
| platforms | Set<Platform> | Ao menos 1 plataforma |
| averageUserRating | BigDecimal | 0.00 a 5.00; calculado/desnormalizado |
| totalRatings | int | Contador desnormalizado |
| deletedAt | Instant | Nullable; soft delete |
| createdAt | Instant | Imutavel |
| updatedAt | Instant | |

**Invariantes:**
- Titulo deve ser unico
- platformScore entre 0.0 e 10.0
- Ao menos uma categoria e uma plataforma obrigatorias
- Jogo deletado nao aparece em listagens publicas

**Eventos de dominio:**
- `GameCreatedEvent`
- `GameUpdatedEvent`
- `GameDeletedEvent`

**Value Objects:**
- `GameId` - wrapper UUID
- `Category` - enum: ACAO, RPG, LUTA, ESPORTE, AVENTURA, ESTRATEGIA, SIMULACAO, TERROR, PLATAFORMA, CORRIDA, PUZZLE, OUTROS
- `Platform` - enum: PC, PLAYSTATION, XBOX, NINTENDO, MOBILE, OUTROS
- `PlatformScore` - BigDecimal com validacao de range

---

### 2.3 Agregado: Rating

**Raiz do Agregado:** `Rating`  
**Pacote:** `br.com.andersondev.domain.rating`

**Entidade Rating:**

| Campo | Tipo | Restricoes |
|---|---|---|
| id | RatingId (UUID) | Imutavel |
| gameId | GameId | Nao nulo |
| userId | UserId | Nao nulo |
| stars | int | 0 a 5 inclusive |
| ratedAt | Instant | Atualizado a cada mudanca |

**Invariantes:**
- Um usuario pode ter apenas uma avaliacao por jogo (unico por gameId+userId)
- Stars entre 0 e 5
- Apos salvar/atualizar, o Game deve ter `averageUserRating` e `totalRatings` atualizados

**Eventos de dominio:**
- `GameRatedEvent` - payload: gameId, userId, stars, isNewRating (bool)

**Value Objects:**
- `RatingId` - wrapper UUID
- `Stars` - int com validacao 0-5

---

### 2.4 Agregado: ShelfItem

**Raiz do Agregado:** `ShelfItem`  
**Pacote:** `br.com.andersondev.domain.shelf`

**Entidade ShelfItem:**

| Campo | Tipo | Restricoes |
|---|---|---|
| id | ShelfItemId (UUID) | Imutavel |
| userId | UserId | Nao nulo |
| gameId | GameId | Nao nulo |
| status | ShelfStatus (enum) | Nao nulo |
| addedAt | Instant | Imutavel, data de adicao |
| updatedAt | Instant | |

**Invariantes:**
- Par (userId, gameId) deve ser unico na estante
- Status obrigatorio

**Value Objects:**
- `ShelfItemId` - wrapper UUID
- `ShelfStatus` - enum: JA_TENHO, PRETENDO_PEGAR, FAVORITO

---

### 2.5 Agregado: Follow

**Raiz do Agregado:** `Follow`  
**Pacote:** `br.com.andersondev.domain.social`

**Entidade Follow:**

| Campo | Tipo | Restricoes |
|---|---|---|
| followerId | UserId | Nao nulo |
| followedId | UserId | Nao nulo |
| followedAt | Instant | Imutavel |

**Invariantes:**
- followerId != followedId
- Par (followerId, followedId) unico

**Observacao:** Nao ha ID proprio; a chave composta (followerId, followedId) e suficiente como identificador.

---

### 2.6 Agregado: Thread

**Raiz do Agregado:** `Thread`  
**Pacote:** `br.com.andersondev.domain.discussion`

**Entidade Thread:**

| Campo | Tipo | Restricoes |
|---|---|---|
| id | ThreadId (UUID) | Imutavel |
| gameId | GameId | Nao nulo |
| authorId | UserId | Nao nulo |
| title | String | Nao nulo, 5-200 chars |
| content | String | Nao nulo, 10-5000 chars |
| likeCount | int | >= 0; desnormalizado |
| replyCount | int | >= 0; desnormalizado |
| createdAt | Instant | Imutavel |
| updatedAt | Instant | |

**Entidade Reply (dentro do agregado Thread):**

| Campo | Tipo | Restricoes |
|---|---|---|
| id | ReplyId (UUID) | Imutavel |
| threadId | ThreadId | Nao nulo |
| authorId | UserId | Nao nulo |
| content | String | Nao nulo, 1-2000 chars |
| likeCount | int | >= 0; desnormalizado |
| createdAt | Instant | Imutavel |

**Entidade ThreadLike:**

| Campo | Tipo | |
|---|---|---|
| threadId | ThreadId | |
| userId | UserId | |
| likedAt | Instant | |

**Entidade ReplyLike:**

| Campo | Tipo | |
|---|---|---|
| replyId | ReplyId | |
| userId | UserId | |
| likedAt | Instant | |

**Invariantes:**
- Um usuario pode dar um unico like por thread/reply
- Like de usuario banido e rejeitado no momento da requisicao
- Reply pertence a exatamente uma Thread

**Eventos de dominio:**
- `ThreadCreatedEvent`
- `ReplyAddedEvent`

---

### 2.7 Agregado: News

**Raiz do Agregado:** `News`  
**Pacote:** `br.com.andersondev.domain.news`

**Entidade News:**

| Campo | Tipo | Restricoes |
|---|---|---|
| id | NewsId (UUID) | Imutavel |
| title | String | Nao nulo, 5-300 chars |
| summary | String | Nao nulo, 10-500 chars |
| content | String | Nao nulo, min 50 chars |
| coverImageUrl | String | Opcional, URL |
| authorId | UserId | Nao nulo (ADMIN) |
| relatedGameId | GameId | Opcional |
| publishedAt | Instant | Imutavel |
| updatedAt | Instant | |

---

### 2.8 Agregado: Report (Denuncia)

**Raiz do Agregado:** `Report`  
**Pacote:** `br.com.andersondev.domain.moderation`

**Entidade Report:**

| Campo | Tipo | Restricoes |
|---|---|---|
| id | ReportId (UUID) | Imutavel |
| reportedUserId | UserId | Nao nulo |
| reportedById | UserId | Nao nulo |
| reason | ReportReason (enum) | Nao nulo |
| description | String | Opcional, max 1000 chars |
| status | ReportStatus (enum) | Default PENDING |
| adminNote | String | Opcional; preenchido ao resolver |
| resolvedBy | UserId | Nullable; ADMIN que resolveu |
| resolvedAt | Instant | Nullable |
| createdAt | Instant | Imutavel |

**Invariantes:**
- reportedUserId != reportedById
- Um usuario nao pode ter multiplas denuncias abertas do mesmo denunciante (PENDING) para o mesmo denunciado

**Value Objects:**
- `ReportReason` - enum: ABUSIVE_BEHAVIOR, SPAM, HARASSMENT, INAPPROPRIATE_CONTENT, OTHER
- `ReportStatus` - enum: PENDING, APPROVED, REJECTED

---

## 3. Value Objects Compartilhados

**Pacote:** `br.com.andersondev.domain.shared`

| Value Object | Descricao |
|---|---|
| `UserId` | UUID imutavel de usuario |
| `GameId` | UUID imutavel de jogo |
| `Email` | Valida formato; case-insensitive |
| `AvatarUrl` | Valida que e URL; opcional |
| `PageRequest` | page, size, sort para paginacao |

---

## 4. Portas (Interfaces de Repositorio e Servicos Externos)

**Pacote:** `br.com.andersondev.domain.*.port`

### Portas de Repositorio (saida)
- `UserRepository` - save, findById, findByEmail, existsByEmail
- `GameRepository` - save, findById, findAll(filtros, pageable), existsByTitle, delete
- `RatingRepository` - save, findByGameIdAndUserId, findAverageByGameId
- `ShelfItemRepository` - save, findByUserIdAndGameId, findByUserId(pageable), delete
- `FollowRepository` - save, existsByFollowerIdAndFollowedId, deleteByFollowerIdAndFollowedId, countByFollowedId, countByFollowerId
- `ThreadRepository` - save, findById, findByGameId(pageable)
- `ReplyRepository` - save, findByThreadId(pageable)
- `ThreadLikeRepository` - save, existsByThreadIdAndUserId, deleteByThreadIdAndUserId
- `ReplyLikeRepository` - save, existsByReplyIdAndUserId, deleteByReplyIdAndUserId
- `NewsRepository` - save, findById, findAll(pageable), findByGameId(pageable), delete
- `ReportRepository` - save, findById, findAll(status, pageable)
- `RefreshTokenRepository` - save, findByTokenHash, revokeByUserId
- `TokenBlocklistRepository` - save, existsByJti, deleteExpired
- `PasswordResetTokenRepository` - save, findByTokenHash, deleteExpired

### Portas de Servico Externo (saida)
- `EmailPort` - sendPasswordResetEmail(email, resetLink)
- `PasswordHasherPort` - hash(rawPassword), matches(rawPassword, hash)

---

## 5. Excecoes de Dominio

**Pacote:** `br.com.andersondev.domain.exception`

| Excecao | HTTP | Descricao |
|---|---|---|
| `EntityNotFoundException` | 404 | Entidade nao encontrada |
| `DuplicateEntityException` | 409 | Violacao de unicidade |
| `InvalidCredentialsException` | 401 | Credenciais invalidas |
| `UserBannedException` | 403 | Usuario banido |
| `TokenExpiredException` | 401 | Token expirado |
| `TokenAlreadyUsedException` | 400 | Token ja utilizado |
| `InvalidRangeException` | 422 | Valor fora do range permitido |
| `SelfReferenceException` | 400 | Operacao invalida consigo mesmo |
| `BusinessRuleViolationException` | 422 | Regra de negocio violada (generica) |
| `ForbiddenOperationException` | 403 | Operacao nao permitida para o role |
