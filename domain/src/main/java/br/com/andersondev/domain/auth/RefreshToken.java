package br.com.andersondev.domain.auth;

import br.com.andersondev.domain.shared.IdUtils;
import br.com.andersondev.domain.user.UserId;

import java.time.Instant;

/**
 * Refresh token opaco (UUID), persistido apenas como hash (SHA-256).
 * Rotacionado a cada uso (revoked_at) conforme ADR-0003.
 */
public class RefreshToken {

    private final String id;
    private final UserId userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private Instant revokedAt;
    private final String deviceInfo;
    private final Instant createdAt;

    private RefreshToken(
            final String id,
            final UserId userId,
            final String tokenHash,
            final Instant expiresAt,
            final Instant revokedAt,
            final String deviceInfo,
            final Instant createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.deviceInfo = deviceInfo;
        this.createdAt = createdAt;
    }

    public static RefreshToken issue(
            final UserId userId,
            final String tokenHash,
            final Instant expiresAt,
            final String deviceInfo
    ) {
        return new RefreshToken(
                IdUtils.uuid(), userId, tokenHash, expiresAt, null, deviceInfo, Instant.now());
    }

    public static RefreshToken with(
            final String id,
            final UserId userId,
            final String tokenHash,
            final Instant expiresAt,
            final Instant revokedAt,
            final String deviceInfo,
            final Instant createdAt
    ) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revokedAt, deviceInfo, createdAt);
    }

    public boolean isExpired(final Instant now) {
        return now.isAfter(this.expiresAt);
    }

    public boolean isRevoked() {
        return this.revokedAt != null;
    }

    public boolean isUsable(final Instant now) {
        return !isExpired(now) && !isRevoked();
    }

    public void revoke() {
        if (this.revokedAt == null) {
            this.revokedAt = Instant.now();
        }
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

    public Instant getRevokedAt() {
        return this.revokedAt;
    }

    public String getDeviceInfo() {
        return this.deviceInfo;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }
}
