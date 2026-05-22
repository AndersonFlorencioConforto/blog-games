package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Violacao de unicidade. Mapeada para 409.
 */
public class DuplicateEntityException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private DuplicateEntityException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static DuplicateEntityException with(final String message) {
        return new DuplicateEntityException(message);
    }
}
