package br.com.andersondev.domain.shelf;

import br.com.andersondev.domain.shared.Identifier;
import br.com.andersondev.domain.shared.IdUtils;

import java.util.Objects;

/**
 * Identificador imutavel de item de estante (UUID v4).
 */
public final class ShelfItemId extends Identifier {

    private final String value;

    private ShelfItemId(final String value) {
        this.value = Objects.requireNonNull(value, "'ShelfItemId' nao pode ser nulo");
    }

    public static ShelfItemId unique() {
        return new ShelfItemId(IdUtils.uuid());
    }

    public static ShelfItemId from(final String value) {
        return new ShelfItemId(value);
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
        final ShelfItemId that = (ShelfItemId) o;
        return Objects.equals(value, that.value);
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
