package br.com.andersondev.domain.auth;

import br.com.andersondev.domain.shared.IdUtils;
import br.com.andersondev.domain.user.UserId;

import java.time.Instant;

/**
 * Token de recuperacao de senha (UUID), persistido apenas como hash (SHA-256).
 * Uso unico, expiracao de 1 hora (ADR-0003 / risco de reutilizacao).
 */
public class PasswordResetToken {

    private final String id;
    private final UserId userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private Instant usedAt;
    private final Instant createdAt;

    private PasswordResetToken(
            final String id,
            final UserId userId,
            final String tokenHash,
            final Instant expiresAt,
            final Instant usedAt,
            final Instant createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }

    public static PasswordResetToken issue(
            final UserId userId,
            final String tokenHash,
            final Instant expiresAt
    ) {
        return new PasswordResetToken(
                IdUtils.uuid(), userId, tokenHash, expiresAt, null, Instant.now());
    }

    public static PasswordResetToken with(
            final String id,
            final UserId userId,
            final String tokenHash,
            final Instant expiresAt,
            final Instant usedAt,
            final Instant createdAt
    ) {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, usedAt, createdAt);
    }

    public boolean isExpired(final Instant now) {
        return now.isAfter(this.expiresAt);
    }

    public boolean isUsed() {
        return this.usedAt != null;
    }

    public void markUsed() {
        this.usedAt = Instant.now();
    }

    public String getId() {
        return this.id;
    }

    public UserId getUserId() {
        return this.userId;
    }

    public String getTokenHash() {
        return this.tokenHash;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public Instant getUsedAt() {
        return this.usedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }
}
