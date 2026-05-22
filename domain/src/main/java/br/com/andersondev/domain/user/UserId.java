package br.com.andersondev.domain.user;

import br.com.andersondev.domain.shared.Identifier;
import br.com.andersondev.domain.shared.IdUtils;

import java.util.Objects;

/**
 * Identificador imutavel de usuario (UUID v4).
 */
public final class UserId extends Identifier {

    private final String value;

    private UserId(final String value) {
        this.value = Objects.requireNonNull(value, "'UserId' nao pode ser nulo");
    }

    public static UserId unique() {
        return new UserId(IdUtils.uuid());
    }

    public static UserId from(final String value) {
        return new UserId(value);
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
        final UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
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
