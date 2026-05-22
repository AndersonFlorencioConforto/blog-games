package br.com.andersondev.domain.moderation;

import br.com.andersondev.domain.shared.IdUtils;
import br.com.andersondev.domain.shared.Identifier;

import java.util.Objects;

/**
 * Identificador imutavel de Report (UUID v4).
 */
public final class ReportId extends Identifier {

    private final String value;

    private ReportId(final String value) {
        this.value = Objects.requireNonNull(value, "'ReportId' nao pode ser nulo");
    }

    public static ReportId unique() {
        return new ReportId(IdUtils.uuid());
    }

    public static ReportId from(final String value) {
        return new ReportId(value);
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
        final ReportId reportId = (ReportId) o;
        return Objects.equals(value, reportId.value);
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
