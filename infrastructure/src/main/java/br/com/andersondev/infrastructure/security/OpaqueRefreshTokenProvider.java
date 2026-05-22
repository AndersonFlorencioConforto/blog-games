package br.com.andersondev.infrastructure.security;

import br.com.andersondev.application.auth.port.RefreshTokenProvider;
import br.com.andersondev.domain.shared.IdUtils;
import br.com.andersondev.infrastructure.config.JwtProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Gera refresh tokens opacos (UUID) e calcula seu hash (SHA-256) para persistencia.
 */
@Component
public class OpaqueRefreshTokenProvider implements RefreshTokenProvider {

    private final JwtProperties properties;

    public OpaqueRefreshTokenProvider(final JwtProperties properties) {
        this.properties = properties;
    }

    @Override
    public GeneratedRefreshToken generate() {
        final var rawValue = IdUtils.uuid();
        final var expiresAt = Instant.now().plusSeconds(this.properties.getRefreshTokenExpiration());
        return new GeneratedRefreshToken(rawValue, Sha256Hasher.hash(rawValue), expiresAt);
    }

    @Override
    public String hash(final String rawToken) {
        return Sha256Hasher.hash(rawToken);
    }
}
