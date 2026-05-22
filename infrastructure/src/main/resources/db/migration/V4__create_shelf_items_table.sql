-- V4: tabela da estante do usuario (data-strategy / domain-catalog 2.4).
-- Unicidade (user_id, game_id): um item por par usuario+jogo (S-02).
-- SQL portavel entre PostgreSQL (dev/staging/prod) e H2 (compat PostgreSQL).

CREATE TABLE shelf_items (
    id         VARCHAR(36) PRIMARY KEY,
    user_id    VARCHAR(36) NOT NULL,
    game_id    VARCHAR(36) NOT NULL,
    status     VARCHAR(30) NOT NULL,
    added_at   TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL,
    CONSTRAINT uk_shelf_items_user_game UNIQUE (user_id, game_id),
    CONSTRAINT fk_shelf_items_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_shelf_items_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE
);

CREATE INDEX idx_shelf_items_user_id ON shelf_items (user_id);
CREATE INDEX idx_shelf_items_user_status ON shelf_items (user_id, status);
