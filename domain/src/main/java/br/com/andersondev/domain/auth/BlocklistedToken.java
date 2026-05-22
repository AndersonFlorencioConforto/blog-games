package br.com.andersondev.domain.auth;

import java.time.Instant;

/**
 * Entrada da denylist de access tokens. Guarda o jti (JWT ID) ate sua
 * expiracao natural para permitir invalidacao real no logout (ADR-0003).
 */
public class BlocklistedToken {

    private final String jti;
    private final Instant expiresAt;
    private final Instant blockedAt;

    private BlocklistedToken(final String jti, final Instant expiresAt, final Instant blockedAt) {
        this.jti = jti;
        this.expiresAt = expiresAt;
        this.blockedAt = blockedAt;
    }

    public static BlocklistedToken of(final String jti, final Instant expiresAt) {
        return new BlocklistedToken(jti, expiresAt, Instant.now());
    }

    public static BlocklistedToken with(final String jti, final Instant expiresAt, final Instant blockedAt) {
        return new BlocklistedToken(jti, expiresAt, blockedAt);
    }

    public String getJti() {
        return this.jti;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public Instant getBlockedAt() {
        return this.blockedAt;
    }
}
