package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Excecao base para regras de negocio violadas. Mapeada para 422 por padrao.
 */
public class DomainException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    protected final transient List<Error> errors;

    protected DomainException(final String message, final List<Error> errors) {
        super(message);
        this.errors = errors;
    }

    public static DomainException with(final Error error) {
        return new DomainException(error.message(), List.of(error));
    }

    public static DomainException with(final List<Error> errors) {
        final var message = errors.isEmpty() ? "Domain validation error" : errors.getFirst().message();
        return new DomainException(message, errors);
    }

    public List<Error> getErrors() {
        return this.errors;
    }
}
