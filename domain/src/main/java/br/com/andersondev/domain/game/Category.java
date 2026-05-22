package br.com.andersondev.domain.game;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.validation.Error;

/**
 * Categoria de jogo (domain-catalog 2.2). G-07.
 */
public enum Category {

    ACAO,
    RPG,
    LUTA,
    ESPORTE,
    AVENTURA,
    ESTRATEGIA,
    SIMULACAO,
    TERROR,
    PLATAFORMA,
    CORRIDA,
    PUZZLE,
    OUTROS;

    public static Category of(final String value) {
        if (value == null || value.isBlank()) {
            throw DomainException.with(new Error("'category' e obrigatorio"));
        }
        try {
            return Category.valueOf(value.trim().toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw DomainException.with(new Error("'category' invalida: " + value));
        }
    }
}
