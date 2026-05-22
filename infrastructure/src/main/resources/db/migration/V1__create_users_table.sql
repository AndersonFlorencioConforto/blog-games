-- V1: tabela de usuarios (data-strategy / domain-catalog User).
-- SQL portavel entre PostgreSQL (dev/staging/prod) e H2 (compat PostgreSQL).
CREATE TABLE users (
    id            VARCHAR(36)  PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
    bio           VARCHAR(500),
    avatar_url    VARCHAR(500),
    banned_until  TIMESTAMP,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);
