package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Operacao nao permitida para o role/ownership do usuario. Mapeada para 403.
 */
public class ForbiddenOperationException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private ForbiddenOperationException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static ForbiddenOperationException with(final String message) {
        return new ForbiddenOperationException(message);
    }
}
