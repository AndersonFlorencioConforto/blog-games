package br.com.andersondev.domain.game;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.exception.InvalidRangeException;
import br.com.andersondev.domain.shared.ValueObject;
import br.com.andersondev.domain.validation.Error;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object da nota editorial da plataforma. G-04.
 * Range 0.0 a 10.0 (inclusive), normalizado para duas casas decimais.
 */
public final class PlatformScore extends ValueObject {

    private static final BigDecimal MIN = BigDecimal.ZERO;
    private static final BigDecimal MAX = BigDecimal.TEN;
    private static final int SCALE = 2;

    private final BigDecimal value;

    private PlatformScore(final BigDecimal value) {
        this.value = value;
    }

    public static PlatformScore of(final BigDecimal rawValue) {
        if (rawValue == null) {
            throw DomainException.with(new Error("'platformScore' e obrigatorio"));
        }
        final var normalized = rawValue.setScale(SCALE, RoundingMode.HALF_UP);
        if (normalized.compareTo(MIN) < 0 || normalized.compareTo(MAX) > 0) {
            throw InvalidRangeException.with("'platformScore' deve estar entre 0.0 e 10.0");
        }
        return new PlatformScore(normalized);
    }

    public BigDecimal getValue() {
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
        final PlatformScore that = (PlatformScore) o;
        return this.value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return this.value.toPlainString();
    }
}
