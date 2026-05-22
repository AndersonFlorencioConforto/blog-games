package br.com.andersondev.domain.news;

import br.com.andersondev.domain.shared.Identifier;
import br.com.andersondev.domain.shared.IdUtils;

import java.util.Objects;

/**
 * Identificador imutavel de News (UUID v4).
 */
public final class NewsId extends Identifier {

    private final String value;

    private NewsId(final String value) {
        this.value = Objects.requireNonNull(value, "'NewsId' nao pode ser nulo");
    }

    public static NewsId unique() {
        return new NewsId(IdUtils.uuid());
    }

    public static NewsId from(final String value) {
        return new NewsId(value);
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
        final NewsId newsId = (NewsId) o;
        return Objects.equals(value, newsId.value);
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
