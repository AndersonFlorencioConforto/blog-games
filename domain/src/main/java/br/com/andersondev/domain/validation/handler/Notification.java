package br.com.andersondev.domain.validation.handler;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Acumulador de erros de validacao. Permite executar validacoes que podem
 * lancar {@link DomainException} e coletar todos os erros antes de decidir
 * o que fazer (ex.: lancar uma excecao agregada no caso de uso).
 */
public class Notification implements ValidationHandler {

    private final List<Error> errors;

    private Notification(final List<Error> errors) {
        this.errors = errors;
    }

    public static Notification create() {
        return new Notification(new ArrayList<>());
    }

    public static Notification create(final Error error) {
        return new Notification(new ArrayList<>()).append(error);
    }

    public static Notification create(final Throwable throwable) {
        return create(new Error(throwable.getMessage()));
    }

    @Override
    public Notification append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Notification append(final ValidationHandler handler) {
        this.errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(final Validation<T> validation) {
        try {
            return validation.validate();
        } catch (final DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (final Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }
        return null;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
