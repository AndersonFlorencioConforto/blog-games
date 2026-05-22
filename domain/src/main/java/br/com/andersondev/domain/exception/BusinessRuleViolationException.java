package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Regra de negocio generica violada. Mapeada para 422.
 */
public class BusinessRuleViolationException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private BusinessRuleViolationException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static BusinessRuleViolationException with(final String message) {
        return new BusinessRuleViolationException(message);
    }
}
