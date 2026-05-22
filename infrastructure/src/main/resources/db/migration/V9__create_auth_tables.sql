-- V9: tabelas de autenticacao (ADR-0003 / data-strategy).
-- Numeracao alinhada ao plano de migrations do data-strategy (V2..V8 ficam para as
-- demais fatias verticais: jogos, ratings, estante, social, threads, noticias, denuncias).

CREATE TABLE refresh_tokens (
    id          VARCHAR(36) PRIMARY KEY,
    user_id     VARCHAR(36) NOT NULL,
    token_hash  VARCHAR(64) NOT NULL UNIQUE,
    expires_at  TIMESTAMP   NOT NULL,
    revoked_at  TIMESTAMP,
    device_info VARCHAR(255),
    created_at  TIMESTAMP   NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_hash ON refresh_tokens (token_hash);

CREATE TABLE token_blocklist (
    jti        VARCHAR(36) PRIMARY KEY,
    expires_at TIMESTAMP   NOT NULL,
    blocked_at TIMESTAMP   NOT NULL
);

CREATE INDEX idx_token_blocklist_expires ON token_blocklist (expires_at);

CREATE TABLE password_reset_tokens (
    id         VARCHAR(36) PRIMARY KEY,
    user_id    VARCHAR(36) NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP   NOT NULL,
    used_at    TIMESTAMP,
    created_at TIMESTAMP   NOT NULL,
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_tokens_user ON password_reset_tokens (user_id);
