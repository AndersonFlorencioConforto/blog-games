package br.com.andersondev.infrastructure.security;

import br.com.andersondev.domain.user.port.PasswordHasherPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter de hash de senha usando BCrypt com fator de custo 12 (NFR / security-matrix).
 */
@Component
public class BCryptPasswordHasher implements PasswordHasherPort {

    private static final int COST_FACTOR = 12;

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordHasher() {
        this.encoder = new BCryptPasswordEncoder(COST_FACTOR);
    }

    @Override
    public String hash(final String rawPassword) {
        return this.encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(final String rawPassword, final String passwordHash) {
        if (rawPassword == null || passwordHash == null) {
            return false;
        }
        return this.encoder.matches(rawPassword, passwordHash);
    }
}
