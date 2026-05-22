package br.com.andersondev.domain.rating;

import br.com.andersondev.domain.shared.Identifier;
import br.com.andersondev.domain.shared.IdUtils;

import java.util.Objects;

/**
 * Identificador imutavel de avaliacao (UUID v4).
 */
public final class RatingId extends Identifier {

    private final String value;

    private RatingId(final String value) {
        this.value = Objects.requireNonNull(value, "'RatingId' nao pode ser nulo");
    }

    public static RatingId unique() {
        return new RatingId(IdUtils.uuid());
    }

    public static RatingId from(final String value) {
        return new RatingId(value);
    }

    @Override
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
        final RatingId ratingId = (RatingId) o;
        return Objects.equals(value, ratingId.value);
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
