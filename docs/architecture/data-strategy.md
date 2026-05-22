# Estrategia de Dados - Blog Games Platform

**Versao:** 1.0  
**Data:** 2026-05-21

---

## 1. Banco de Dados por Profile

| Profile | Banco | Motivo |
|---|---|---|
| `local` | H2 (embedded, file-based) | Zero dependencias externas para dev |
| `dev` | PostgreSQL 16+ | Paridade com producao |
| `staging` | PostgreSQL 16+ | Idem |
| `production` | PostgreSQL 16+ | Confiabilidade, ACID, suporte a indices avancados |

**Ferramenta de migracao:** Flyway (ativo em `dev`, `staging`, `production`; opcional em `local`)

---

## 2. Modelo Relacional

### Tabela: `users`

```sql
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100)  NOT NULL,
    email           VARCHAR(255)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255)  NOT NULL,
    role            VARCHAR(20)   NOT NULL DEFAULT 'USER',
    bio             VARCHAR(500),
    avatar_url      VARCHAR(500),
    banned_until    TIMESTAMP,
    active          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role) WHERE role = 'ADMIN';
```

### Tabela: `games`

```sql
CREATE TABLE games (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title                VARCHAR(200)   NOT NULL UNIQUE,
    description          TEXT           NOT NULL,
    editorial_review     TEXT,
    platform_score       NUMERIC(4,2)   NOT NULL CHECK (platform_score >= 0 AND platform_score <= 10),
    pros                 TEXT[],
    cons                 TEXT[],
    cover_image_url      VARCHAR(500),
    average_user_rating  NUMERIC(3,2)   NOT NULL DEFAULT 0.00,
    total_ratings        INTEGER        NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP,
    created_at           TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_games_title ON games(title);
CREATE INDEX idx_games_deleted_at ON games(deleted_at) WHERE deleted_at IS NULL;
```

**Observacao:** Para compatibilidade com H2, o tipo `TEXT[]` (array PostgreSQL) deve ser mapeado via `@Convert` JPA ou armazenado como JSON string. Ver premissa P-014.

**Premissa P-014:** `pros` e `cons` serao armazenados como `VARCHAR(2000)` com separador pipe (`|`) ou como coluna JSON em PostgreSQL. Decidir antes de implementar Migration V1. Recomendacao: usar `@ElementCollection` JPA com tabela auxiliar para portabilidade total.

### Tabela: `game_categories`

```sql
CREATE TABLE game_categories (
    game_id  UUID        NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    PRIMARY KEY (game_id, category)
);

CREATE INDEX idx_game_categories_category ON game_categories(category);
```

### Tabela: `game_platforms`

```sql
CREATE TABLE game_platforms (
    game_id  UUID        NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    platform VARCHAR(50) NOT NULL,
    PRIMARY KEY (game_id, platform)
);

CREATE INDEX idx_game_platforms_platform ON game_platforms(platform);
```

### Tabela: `ratings`

```sql
CREATE TABLE ratings (
    id        UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    game_id   UUID      NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    user_id   UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    stars     SMALLINT  NOT NULL CHECK (stars >= 0 AND stars <= 5),
    rated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (game_id, user_id)
);

CREATE INDEX idx_ratings_game_id ON ratings(game_id);
CREATE INDEX idx_ratings_user_id ON ratings(user_id);
```

**Estrategia de media:** Campo `average_user_rating` e `total_ratings` em `games` sao atualizados na mesma transacao do upsert de rating. Consulta AVG e feita apenas em background de recalculo periodico (fallback de consistencia).

### Tabela: `shelf_items`

```sql
CREATE TABLE shelf_items (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id    UUID        NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    status     VARCHAR(30) NOT NULL,
    added_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, game_id)
);

CREATE INDEX idx_shelf_items_user_id ON shelf_items(user_id);
CREATE INDEX idx_shelf_items_user_status ON shelf_items(user_id, status);
```

### Tabela: `follows`

```sql
CREATE TABLE follows (
    follower_id UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    followed_id UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    followed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (follower_id, followed_id),
    CHECK (follower_id != followed_id)
);

CREATE INDEX idx_follows_followed_id ON follows(followed_id);
```

### Tabela: `threads`

```sql
CREATE TABLE threads (
    id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    game_id     UUID      NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    author_id   UUID      NOT NULL REFERENCES users(id),
    title       VARCHAR(200) NOT NULL,
    content     TEXT      NOT NULL,
    like_count  INTEGER   NOT NULL DEFAULT 0,
    reply_count INTEGER   NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_threads_game_id ON threads(game_id);
CREATE INDEX idx_threads_author_id ON threads(author_id);
CREATE INDEX idx_threads_game_created ON threads(game_id, created_at DESC);
```

### Tabela: `replies`

```sql
CREATE TABLE replies (
    id         UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    thread_id  UUID      NOT NULL REFERENCES threads(id) ON DELETE CASCADE,
    author_id  UUID      NOT NULL REFERENCES users(id),
    content    TEXT      NOT NULL,
    like_count INTEGER   NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_replies_thread_id ON replies(thread_id);
CREATE INDEX idx_replies_thread_created ON replies(thread_id, created_at ASC);
```

### Tabelas de Likes

```sql
CREATE TABLE thread_likes (
    thread_id UUID      NOT NULL REFERENCES threads(id) ON DELETE CASCADE,
    user_id   UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    liked_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (thread_id, user_id)
);

CREATE TABLE reply_likes (
    reply_id  UUID      NOT NULL REFERENCES replies(id) ON DELETE CASCADE,
    user_id   UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    liked_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (reply_id, user_id)
);
```

### Tabela: `news`

```sql
CREATE TABLE news (
    id               UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    title            VARCHAR(300) NOT NULL,
    summary          VARCHAR(500) NOT NULL,
    content          TEXT      NOT NULL,
    cover_image_url  VARCHAR(500),
    author_id        UUID      NOT NULL REFERENCES users(id),
    related_game_id  UUID      REFERENCES games(id) ON DELETE SET NULL,
    published_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_news_published_at ON news(published_at DESC);
CREATE INDEX idx_news_related_game ON news(related_game_id) WHERE related_game_id IS NOT NULL;
```

### Tabela: `reports`

```sql
CREATE TABLE reports (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    reported_user_id  UUID        NOT NULL REFERENCES users(id),
    reported_by_id    UUID        NOT NULL REFERENCES users(id),
    reason            VARCHAR(50) NOT NULL,
    description       VARCHAR(1000),
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_note        VARCHAR(500),
    resolved_by       UUID        REFERENCES users(id),
    resolved_at       TIMESTAMP,
    created_at        TIMESTAMP   NOT NULL DEFAULT NOW(),
    CHECK (reported_user_id != reported_by_id)
);

CREATE INDEX idx_reports_status ON reports(status) WHERE status = 'PENDING';
CREATE INDEX idx_reports_reported_user ON reports(reported_user_id);
```

### Tabelas de Autenticacao

```sql
CREATE TABLE refresh_tokens (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(64) NOT NULL UNIQUE,
    expires_at  TIMESTAMP   NOT NULL,
    revoked_at  TIMESTAMP,
    device_info VARCHAR(255),
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_hash ON refresh_tokens(token_hash);

CREATE TABLE token_blocklist (
    jti        VARCHAR(36) PRIMARY KEY,
    expires_at TIMESTAMP   NOT NULL,
    blocked_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_token_blocklist_expires ON token_blocklist(expires_at);

CREATE TABLE password_reset_tokens (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP   NOT NULL,
    used_at    TIMESTAMP,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
```

---

## 3. Estrategia de Paginacao

- Padrao: `page=0&size=20` (Spring Pageable)
- Maximo por pagina: 50 itens (validado no application layer)
- Ordenacao default por `createdAt DESC` para listagens de jogos, threads e noticias
- Resposta inclui: `content`, `page`, `size`, `totalElements`, `totalPages`

---

## 4. Consultas Criticas e Indices

| Consulta | Tabela | Indice | Observacao |
|---|---|---|---|
| Filtrar jogos por categoria | `game_categories` | `idx_game_categories_category` | JOIN com games |
| Filtrar jogos por plataforma | `game_platforms` | `idx_game_platforms_platform` | JOIN com games |
| Threads por jogo (paginado) | `threads` | `idx_threads_game_created` | Composto (game_id, created_at) |
| Respostas por thread | `replies` | `idx_replies_thread_created` | |
| Estante por usuario | `shelf_items` | `idx_shelf_items_user_status` | Filtro por status frequente |
| Noticias recentes | `news` | `idx_news_published_at` | Ordenacao DESC |
| Denuncias pendentes | `reports` | `idx_reports_status` (partial) | WHERE status='PENDING' |
| Verificar token na denylist | `token_blocklist` | PK (jti) | Critico por requisicao |

---

## 5. Estrategia de Media de Avaliacoes

**Abordagem escolhida:** Desnormalizacao com atualizacao transacional

- Campos `average_user_rating` e `total_ratings` na tabela `games`
- Atualizados na mesma transacao do INSERT/UPDATE em `ratings`
- Formula: `new_average = ((old_average * old_count) + new_stars) / new_count`
- Em caso de atualizacao de estrelas: recalcular subtraindo o valor anterior
- Job `@Scheduled` semanal recalcula todas as medias via `SELECT AVG(stars) FROM ratings GROUP BY game_id` para corrigir eventuais inconsistencias

---

## 6. Limpeza de Dados Temporarios

Job `@Scheduled(cron = "0 0 3 * * *")` (3h da manha diariamente):

```sql
-- Limpar tokens da denylist ja expirados
DELETE FROM token_blocklist WHERE expires_at < NOW();

-- Limpar tokens de recuperacao de senha expirados
DELETE FROM password_reset_tokens WHERE expires_at < NOW() AND used_at IS NULL;

-- Limpar refresh tokens revogados ha mais de 30 dias
DELETE FROM refresh_tokens WHERE revoked_at < NOW() - INTERVAL '30 days';
```

---

## 7. Migrations Flyway

**Localizacao:** `infrastructure/src/main/resources/db/migration/`

**Nomenclatura:** `V{numero}__{descricao_com_underline}.sql`

```
V1__create_users_table.sql
V2__create_games_tables.sql
V3__create_ratings_table.sql
V4__create_shelf_items_table.sql
V5__create_follows_table.sql
V6__create_threads_and_replies_tables.sql
V7__create_news_table.sql
V8__create_reports_table.sql
V9__create_auth_tables.sql
```

**Configuracao Flyway:**
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=false
spring.flyway.validate-on-migrate=true
```

Para profile `local` com H2:
```properties
spring.flyway.enabled=false
spring.jpa.hibernate.ddl-auto=create-drop
```
