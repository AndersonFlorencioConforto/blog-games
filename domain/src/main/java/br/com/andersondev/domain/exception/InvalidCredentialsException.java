package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Credenciais invalidas. Mapeada para 401.
 * Nao revela qual campo esta incorreto (A-02).
 */
public class InvalidCredentialsException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private InvalidCredentialsException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static InvalidCredentialsException create() {
        return new InvalidCredentialsException("Credenciais invalidas");
    }
}
