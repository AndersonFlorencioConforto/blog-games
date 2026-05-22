package br.com.andersondev.domain.user;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.validation.Error;

/**
 * Politica de senha (regra de dominio, security-matrix / U-04, U-05):
 * minimo 8 chars, ao menos 1 maiuscula, 1 minuscula, 1 numero e 1 caractere especial.
 *
 * Opera sobre a senha em texto puro ANTES do hash. Nao armazena a senha.
 */
public final class PasswordPolicy {

    private static final int MIN_LENGTH = 8;

    private PasswordPolicy() {
    }

    public static void validate(final String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw DomainException.with(new Error("'password' e obrigatorio"));
        }
        if (rawPassword.length() < MIN_LENGTH) {
            throw DomainException.with(new Error("'password' deve ter no minimo 8 caracteres"));
        }
        if (!hasUpperCase(rawPassword)
                || !hasLowerCase(rawPassword)
                || !hasDigit(rawPassword)
                || !hasSpecialChar(rawPassword)) {
            throw DomainException.with(new Error(
                    "'password' deve conter ao menos 1 maiuscula, 1 minuscula, 1 numero e 1 caractere especial"));
        }
    }

    private static boolean hasUpperCase(final String value) {
        return value.chars().anyMatch(Character::isUpperCase);
    }

    private static boolean hasLowerCase(final String value) {
        return value.chars().anyMatch(Character::isLowerCase);
    }

    private static boolean hasDigit(final String value) {
        return value.chars().anyMatch(Character::isDigit);
    }

    private static boolean hasSpecialChar(final String value) {
        return value.chars().anyMatch(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c));
    }
}
