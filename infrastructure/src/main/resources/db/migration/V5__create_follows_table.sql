-- V5: tabela de relacoes sociais de seguir (data-strategy / domain-catalog 2.5).
-- Chave composta (follower_id, followed_id): par unico, sem id proprio.
-- CHECK impede seguir a si mesmo (reforco da invariante de dominio F-01).
-- SQL portavel entre PostgreSQL (dev/staging/prod) e H2 (compat PostgreSQL).

CREATE TABLE follows (
    follower_id VARCHAR(36) NOT NULL,
    followed_id VARCHAR(36) NOT NULL,
    followed_at TIMESTAMP   NOT NULL,
    PRIMARY KEY (follower_id, followed_id),
    CONSTRAINT chk_follows_no_self CHECK (follower_id <> followed_id),
    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_followed FOREIGN KEY (followed_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_follows_followed_id ON follows (followed_id);
CREATE INDEX idx_follows_follower_id ON follows (follower_id);
