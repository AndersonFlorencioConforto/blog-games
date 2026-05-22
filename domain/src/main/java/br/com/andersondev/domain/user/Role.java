package br.com.andersondev.domain.user;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.validation.Error;

/**
 * Papel do usuario no sistema.
 */
public enum Role {

    USER,
    ADMIN;

    public static Role of(final String value) {
        if (value == null || value.isBlank()) {
            throw DomainException.with(new Error("'role' e obrigatorio"));
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw DomainException.with(new Error("'role' invalido: " + value));
        }
    }
}
