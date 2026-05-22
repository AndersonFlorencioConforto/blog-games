package br.com.andersondev.domain.user;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.shared.ValueObject;
import br.com.andersondev.domain.validation.Error;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object Email. Valida formato (RFC 5322 simplificado) e normaliza
 * para lowercase (comparacao case-insensitive). U-02.
 */
public final class Email extends ValueObject {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

    private final String value;

    private Email(final String value) {
        this.value = value;
    }

    public static Email of(final String rawEmail) {
        if (rawEmail == null || rawEmail.isBlank()) {
            throw DomainException.with(new Error("'email' e obrigatorio"));
        }
        final var normalized = rawEmail.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw DomainException.with(new Error("'email' possui formato invalido"));
        }
        return new Email(normalized);
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
