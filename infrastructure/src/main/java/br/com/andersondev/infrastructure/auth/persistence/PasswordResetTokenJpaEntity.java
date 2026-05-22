package br.com.andersondev.infrastructure.auth.persistence;

import br.com.andersondev.domain.auth.PasswordResetToken;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity(name = "PasswordResetToken")
@Table(name = "password_reset_tokens")
public class PasswordResetTokenJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public PasswordResetTokenJpaEntity() {
    }

    public static PasswordResetTokenJpaEntity from(final PasswordResetToken token) {
        final var entity = new PasswordResetTokenJpaEntity();
        entity.id = token.getId();
        entity.userId = token.getUserId().getValue();
        entity.tokenHash = token.getTokenHash();
        entity.expiresAt = token.getExpiresAt();
        entity.usedAt = token.getUsedAt();
        entity.createdAt = token.getCreatedAt();
        return entity;
    }

    public PasswordResetToken toDomain() {
        return PasswordResetToken.with(
                this.id,
                UserId.from(this.userId),
                this.tokenHash,
                this.expiresAt,
                this.usedAt,
                this.createdAt
        );
    }
}
