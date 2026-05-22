package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Token expirado ou invalido. Mapeada para 401.
 */
public class TokenExpiredException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private TokenExpiredException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static TokenExpiredException create() {
        return new TokenExpiredException("Token expirado ou invalido");
    }

    public static TokenExpiredException with(final String message) {
        return new TokenExpiredException(message);
    }
}
