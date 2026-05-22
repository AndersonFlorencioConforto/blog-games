package br.com.andersondev.domain.validation;

import java.util.List;

/**
 * Acumula erros de validacao durante a verificacao de invariantes.
 */
public interface ValidationHandler {

    ValidationHandler append(Error error);

    ValidationHandler append(ValidationHandler handler);

    <T> T validate(Validation<T> validation);

    List<Error> getErrors();

    default boolean hasError() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    default Error firstError() {
        if (getErrors() != null && !getErrors().isEmpty()) {
            return getErrors().getFirst();
        }
        return null;
    }

    interface Validation<T> {
        T validate();
    }
}
