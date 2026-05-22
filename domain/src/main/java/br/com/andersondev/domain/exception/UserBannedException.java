package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.time.Instant;
import java.util.List;

/**
 * Usuario banido nao pode autenticar. Mapeada para 403.
 */
public class UserBannedException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient Instant bannedUntil;

    private UserBannedException(final String message, final Instant bannedUntil) {
        super(message, List.of(new Error(message)));
        this.bannedUntil = bannedUntil;
    }

    public static UserBannedException until(final Instant bannedUntil) {
        return new UserBannedException(
                "Usuario banido ate " + bannedUntil, bannedUntil);
    }

    public Instant getBannedUntil() {
        return this.bannedUntil;
    }
}
