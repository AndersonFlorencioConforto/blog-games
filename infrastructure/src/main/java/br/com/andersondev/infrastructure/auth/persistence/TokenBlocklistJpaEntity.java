package br.com.andersondev.infrastructure.auth.persistence;

import br.com.andersondev.domain.auth.BlocklistedToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity(name = "TokenBlocklist")
@Table(name = "token_blocklist")
public class TokenBlocklistJpaEntity {

    @Id
    @Column(name = "jti", nullable = false, length = 36)
    private String jti;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "blocked_at", nullable = false)
    private Instant blockedAt;

    public TokenBlocklistJpaEntity() {
    }

    public static TokenBlocklistJpaEntity from(final BlocklistedToken token) {
        final var entity = new TokenBlocklistJpaEntity();
        entity.jti = token.getJti();
        entity.expiresAt = token.getExpiresAt();
        entity.blockedAt = token.getBlockedAt();
        return entity;
    }

    public BlocklistedToken toDomain() {
        return BlocklistedToken.with(this.jti, this.expiresAt, this.blockedAt);
    }
}
