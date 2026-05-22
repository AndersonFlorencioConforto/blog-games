package br.com.andersondev.infrastructure.security;

import br.com.andersondev.application.auth.port.ResetTokenProvider;
import br.com.andersondev.domain.shared.IdUtils;
import br.com.andersondev.infrastructure.config.PasswordResetProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Gera tokens de recuperacao de senha opacos (UUID) com hash SHA-256 e expiracao 1h.
 */
@Component
public class OpaqueResetTokenProvider implements ResetTokenProvider {

    private final PasswordResetProperties properties;

    public OpaqueResetTokenProvider(final PasswordResetProperties properties) {
        this.properties = properties;
    }

    @Override
    public GeneratedResetToken generate() {
        final var rawValue = IdUtils.uuid();
        final var expiresAt = Instant.now().plusSeconds(this.properties.getTokenExpiration());
        return new GeneratedResetToken(rawValue, Sha256Hasher.hash(rawValue), expiresAt);
    }

    @Override
    public String hash(final String rawToken) {
        return Sha256Hasher.hash(rawToken);
    }
}
