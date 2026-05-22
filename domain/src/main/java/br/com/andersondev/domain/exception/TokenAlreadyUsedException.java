package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Token de uso unico ja utilizado ou invalido. Mapeada para 400.
 */
public class TokenAlreadyUsedException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private TokenAlreadyUsedException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static TokenAlreadyUsedException create() {
        return new TokenAlreadyUsedException("Token invalido ou ja utilizado");
    }

    public static TokenAlreadyUsedException with(final String message) {
        return new TokenAlreadyUsedException(message);
    }
}
