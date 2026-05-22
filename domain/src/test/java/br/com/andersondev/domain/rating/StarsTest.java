package br.com.andersondev.domain.rating;

import br.com.andersondev.domain.exception.InvalidRangeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StarsTest {

    @Test
    void givenValueInRange_whenOf_thenCreates() {
        assertEquals(0, Stars.of(0).getValue());
        assertEquals(3, Stars.of(3).getValue());
        assertEquals(5, Stars.of(5).getValue());
    }

    @Test
    void givenValueBelowZero_whenOf_thenThrowsInvalidRange() {
        assertThrows(InvalidRangeException.class, () -> Stars.of(-1));
    }

    @Test
    void givenValueAboveFive_whenOf_thenThrowsInvalidRange() {
        assertThrows(InvalidRangeException.class, () -> Stars.of(6));
    }

    @Test
    void givenSameValue_whenEquals_thenTrue() {
        assertEquals(Stars.of(4), Stars.of(4));
    }
}
