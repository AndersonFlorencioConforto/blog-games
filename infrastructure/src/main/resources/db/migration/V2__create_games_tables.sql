-- V2: tabelas do catalogo de jogos (data-strategy / domain-catalog 2.2).
-- SQL portavel entre PostgreSQL (dev/staging/prod) e H2 (compat PostgreSQL).
-- pros/cons como tabelas auxiliares ordenadas (P-014); categorias/plataformas como
-- tabelas auxiliares de enum. IDs como VARCHAR(36) (mesmo padrao do auth).

CREATE TABLE games (
    id                  VARCHAR(36)   PRIMARY KEY,
    title               VARCHAR(200)  NOT NULL UNIQUE,
    description         VARCHAR(5000) NOT NULL,
    editorial_review    VARCHAR(10000),
    platform_score      NUMERIC(4,2)  NOT NULL CHECK (platform_score >= 0 AND platform_score <= 10),
    cover_image_url     VARCHAR(500),
    average_user_rating NUMERIC(3,2)  NOT NULL DEFAULT 0.00,
    total_ratings       INTEGER       NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMP,
    created_at          TIMESTAMP     NOT NULL,
    updated_at          TIMESTAMP     NOT NULL
);

CREATE INDEX idx_games_title ON games (title);
CREATE INDEX idx_games_deleted_at ON games (deleted_at);
CREATE INDEX idx_games_created_at ON games (created_at);

-- pros (lista ordenada, max 10 itens; cada item <= 200 chars - validado no dominio)
CREATE TABLE game_pros (
    game_id  VARCHAR(36) NOT NULL,
    position INTEGER     NOT NULL,
    value    VARCHAR(200),
    PRIMARY KEY (game_id, position),
    CONSTRAINT fk_game_pros_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE
);

-- cons (lista ordenada)
CREATE TABLE game_cons (
    game_id  VARCHAR(36) NOT NULL,
    position INTEGER     NOT NULL,
    value    VARCHAR(200),
    PRIMARY KEY (game_id, position),
    CONSTRAINT fk_game_cons_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE
);

-- categorias (enum como string)
CREATE TABLE game_categories (
    game_id  VARCHAR(36) NOT NULL,
    category VARCHAR(50) NOT NULL,
    PRIMARY KEY (game_id, category),
    CONSTRAINT fk_game_categories_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE
);

CREATE INDEX idx_game_categories_category ON game_categories (category);

-- plataformas (enum como string)
CREATE TABLE game_platforms (
    game_id  VARCHAR(36) NOT NULL,
    platform VARCHAR(50) NOT NULL,
    PRIMARY KEY (game_id, platform),
    CONSTRAINT fk_game_platforms_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE
);

CREATE INDEX idx_game_platforms_platform ON game_platforms (platform);
