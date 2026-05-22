package br.com.andersondev.infrastructure.auth.persistence;

import br.com.andersondev.domain.auth.PasswordResetToken;
import br.com.andersondev.domain.auth.port.PasswordResetTokenGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
public class PasswordResetTokenJpaGateway implements PasswordResetTokenGateway {

    private final PasswordResetTokenRepository repository;

    public PasswordResetTokenJpaGateway(final PasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public PasswordResetToken save(final PasswordResetToken token) {
        return this.repository.save(PasswordResetTokenJpaEntity.from(token)).toDomain();
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(final String tokenHash) {
        return this.repository.findByTokenHash(tokenHash).map(PasswordResetTokenJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public int deleteExpired() {
        return this.repository.deleteExpired(Instant.now());
    }
}
