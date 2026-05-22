package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Valor fora do range permitido. Mapeada para 422.
 */
public class InvalidRangeException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private InvalidRangeException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static InvalidRangeException with(final String message) {
        return new InvalidRangeException(message);
    }
}
