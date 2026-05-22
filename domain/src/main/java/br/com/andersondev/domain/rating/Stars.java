package br.com.andersondev.domain.rating;

import br.com.andersondev.domain.exception.InvalidRangeException;
import br.com.andersondev.domain.shared.ValueObject;

import java.util.Objects;

/**
 * Value Object da nota de avaliacao do usuario. R-01.
 * Inteiro entre 0 e 5 (inclusive).
 */
public final class Stars extends ValueObject {

    private static final int MIN = 0;
    private static final int MAX = 5;

    private final int value;

    private Stars(final int value) {
        this.value = value;
    }

    public static Stars of(final int value) {
        if (value < MIN || value > MAX) {
            throw InvalidRangeException.with("'stars' deve ser um inteiro entre 0 e 5");
        }
        return new Stars(value);
    }

    public int getValue() {
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
        final Stars stars = (Stars) o;
        return value == stars.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
