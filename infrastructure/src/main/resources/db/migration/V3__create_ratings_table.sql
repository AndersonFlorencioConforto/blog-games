-- V3: tabela de avaliacoes (data-strategy / domain-catalog 2.3).
-- Unicidade (game_id, user_id): uma avaliacao por par jogo+usuario (R-03).
-- A media desnormalizada de games e atualizada na mesma transacao do upsert.

CREATE TABLE ratings (
    id       VARCHAR(36) PRIMARY KEY,
    game_id  VARCHAR(36) NOT NULL,
    user_id  VARCHAR(36) NOT NULL,
    stars    SMALLINT    NOT NULL CHECK (stars >= 0 AND stars <= 5),
    rated_at TIMESTAMP   NOT NULL,
    CONSTRAINT uk_ratings_game_user UNIQUE (game_id, user_id),
    CONSTRAINT fk_ratings_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE,
    CONSTRAINT fk_ratings_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_ratings_game_id ON ratings (game_id);
CREATE INDEX idx_ratings_user_id ON ratings (user_id);
