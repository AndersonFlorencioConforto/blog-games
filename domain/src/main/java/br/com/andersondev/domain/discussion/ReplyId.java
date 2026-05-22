package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.shared.Identifier;
import br.com.andersondev.domain.shared.IdUtils;

import java.util.Objects;

/**
 * Identificador imutavel de Reply (UUID v4).
 */
public final class ReplyId extends Identifier {

    private final String value;

    private ReplyId(final String value) {
        this.value = Objects.requireNonNull(value, "'ReplyId' nao pode ser nulo");
    }

    public static ReplyId unique() {
        return new ReplyId(IdUtils.uuid());
    }

    public static ReplyId from(final String value) {
        return new ReplyId(value);
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
        final ReplyId that = (ReplyId) o;
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
