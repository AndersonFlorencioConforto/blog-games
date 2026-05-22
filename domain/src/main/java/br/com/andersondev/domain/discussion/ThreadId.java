package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.shared.Identifier;
import br.com.andersondev.domain.shared.IdUtils;

import java.util.Objects;

/**
 * Identificador imutavel de Thread (UUID v4).
 */
public final class ThreadId extends Identifier {

    private final String value;

    private ThreadId(final String value) {
        this.value = Objects.requireNonNull(value, "'ThreadId' nao pode ser nulo");
    }

    public static ThreadId unique() {
        return new ThreadId(IdUtils.uuid());
    }

    public static ThreadId from(final String value) {
        return new ThreadId(value);
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
        final ThreadId that = (ThreadId) o;
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
