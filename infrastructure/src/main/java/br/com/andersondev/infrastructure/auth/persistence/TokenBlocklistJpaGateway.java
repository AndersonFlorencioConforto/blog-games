package br.com.andersondev.infrastructure.auth.persistence;

import br.com.andersondev.domain.auth.BlocklistedToken;
import br.com.andersondev.domain.auth.port.TokenBlocklistGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class TokenBlocklistJpaGateway implements TokenBlocklistGateway {

    private final TokenBlocklistRepository repository;

    public TokenBlocklistJpaGateway(final TokenBlocklistRepository repository) {
        this.repository = repository;
    }

    @Override
    public BlocklistedToken save(final BlocklistedToken token) {
        return this.repository.save(TokenBlocklistJpaEntity.from(token)).toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByJti(final String jti) {
        return this.repository.existsByJti(jti);
    }

    @Override
    @Transactional
    public int deleteExpired() {
        return this.repository.deleteExpired(Instant.now());
    }
}
