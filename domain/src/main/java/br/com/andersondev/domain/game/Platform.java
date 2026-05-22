package br.com.andersondev.domain.game;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.validation.Error;

/**
 * Plataforma de jogo (domain-catalog 2.2). G-08.
 */
public enum Platform {

    PC,
    PLAYSTATION,
    XBOX,
    NINTENDO,
    MOBILE,
    OUTROS;

    public static Platform of(final String value) {
        if (value == null || value.isBlank()) {
            throw DomainException.with(new Error("'platform' e obrigatorio"));
        }
        try {
            return Platform.valueOf(value.trim().toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw DomainException.with(new Error("'platform' invalida: " + value));
        }
    }
}
