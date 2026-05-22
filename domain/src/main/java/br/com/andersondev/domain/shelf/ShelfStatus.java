package br.com.andersondev.domain.shelf;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.validation.Error;

/**
 * Status de um item na estante do usuario (domain-catalog 2.4 / S-01).
 */
public enum ShelfStatus {

    JA_TENHO,
    PRETENDO_PEGAR,
    FAVORITO;

    public static ShelfStatus of(final String value) {
        if (value == null || value.isBlank()) {
            throw DomainException.with(new Error("'status' e obrigatorio"));
        }
        try {
            return ShelfStatus.valueOf(value.trim().toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw DomainException.with(new Error("'status' invalido: " + value));
        }
    }
}
