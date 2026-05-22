package br.com.andersondev.domain.validation.handler;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.util.List;

/**
 * Handler que lanca {@link DomainException} no primeiro erro encontrado.
 */
public class ThrowsValidationHandler implements ValidationHandler {

    @Override
    public ValidationHandler append(final Error error) {
        throw DomainException.with(error);
    }

    @Override
    public ValidationHandler append(final ValidationHandler handler) {
        throw DomainException.with(handler.getErrors());
    }

    @Override
    public <T> T validate(final Validation<T> validation) {
        try {
            return validation.validate();
        } catch (final Exception ex) {
            throw DomainException.with(new Error(ex.getMessage()));
        }
    }

    @Override
    public List<Error> getErrors() {
        return List.of();
    }
}
