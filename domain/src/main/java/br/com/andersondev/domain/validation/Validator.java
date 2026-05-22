package br.com.andersondev.domain.validation;

/**
 * Base para validadores de dominio. Cada agregado delega suas invariantes
 * para um validador especifico que reporta erros no {@link ValidationHandler}.
 */
public abstract class Validator {

    private final ValidationHandler handler;

    protected Validator(final ValidationHandler handler) {
        this.handler = handler;
    }

    public abstract void validate();

    protected ValidationHandler validationHandler() {
        return this.handler;
    }
}
