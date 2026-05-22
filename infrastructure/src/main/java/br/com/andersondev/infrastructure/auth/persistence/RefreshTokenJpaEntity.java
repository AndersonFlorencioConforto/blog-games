package br.com.andersondev.infrastructure.auth.persistence;

import br.com.andersondev.domain.auth.RefreshToken;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity(name = "RefreshToken")
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public RefreshTokenJpaEntity() {
    }

    public static RefreshTokenJpaEntity from(final RefreshToken token) {
        final var entity = new RefreshTokenJpaEntity();
        entity.id = token.getId();
        entity.userId = token.getUserId().getValue();
        entity.tokenHash = token.getTokenHash();
        entity.expiresAt = token.getExpiresAt();
        entity.revokedAt = token.getRevokedAt();
        entity.deviceInfo = token.getDeviceInfo();
        entity.createdAt = token.getCreatedAt();
        return entity;
    }

    public RefreshToken toDomain() {
        return RefreshToken.with(
                this.id,
                UserId.from(this.userId),
                this.tokenHash,
                this.expiresAt,
                this.revokedAt,
                this.deviceInfo,
                this.createdAt
        );
    }
}
