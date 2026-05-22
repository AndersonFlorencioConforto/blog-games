package br.com.andersondev.domain.game;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.exception.InvalidRangeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformScoreTest {

    @Test
    void givenValidScore_whenOf_thenNormalizesToTwoDecimals() {
        final var score = PlatformScore.of(new BigDecimal("9.5"));

        assertEquals(new BigDecimal("9.50"), score.getValue());
    }

    @Test
    void givenBoundaryScores_whenOf_thenAccepts() {
        assertEquals(new BigDecimal("0.00"), PlatformScore.of(BigDecimal.ZERO).getValue());
        assertEquals(new BigDecimal("10.00"), PlatformScore.of(BigDecimal.TEN).getValue());
    }

    @Test
    void givenScoreAboveMax_whenOf_thenThrowsInvalidRange() {
        assertThrows(InvalidRangeException.class, () -> PlatformScore.of(new BigDecimal("10.01")));
    }

    @Test
    void givenNegativeScore_whenOf_thenThrowsInvalidRange() {
        assertThrows(InvalidRangeException.class, () -> PlatformScore.of(new BigDecimal("-0.01")));
    }

    @Test
    void givenNullScore_whenOf_thenThrowsDomainException() {
        assertThrows(DomainException.class, () -> PlatformScore.of(null));
    }

    @Test
    void givenSameNumericValueDifferentScale_whenEquals_thenTrue() {
        assertEquals(PlatformScore.of(new BigDecimal("8.0")), PlatformScore.of(new BigDecimal("8.00")));
    }
}
