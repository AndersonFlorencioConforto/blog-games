package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Operacao invalida consigo mesmo (ex.: seguir/denunciar a si proprio). Mapeada para 400.
 */
public class SelfReferenceException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private SelfReferenceException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static SelfReferenceException with(final String message) {
        return new SelfReferenceException(message);
    }
}
