package br.com.andersondev.infrastructure.auth.persistence;

import br.com.andersondev.domain.auth.RefreshToken;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.user.UserId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class RefreshTokenJpaGateway implements RefreshTokenGateway {

    private final RefreshTokenRepository repository;

    public RefreshTokenJpaGateway(final RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshToken save(final RefreshToken refreshToken) {
        return this.repository.save(RefreshTokenJpaEntity.from(refreshToken)).toDomain();
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(final String tokenHash) {
        return this.repository.findByTokenHash(tokenHash).map(RefreshTokenJpaEntity::toDomain);
    }

    @Override
    public void revokeAllByUserId(final UserId userId) {
        this.repository.revokeAllByUserId(userId.getValue(), Instant.now());
    }
}
