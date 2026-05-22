package br.com.andersondev.domain.game;

import br.com.andersondev.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformTest {

    @Test
    void givenValidName_whenOf_thenReturnsEnum() {
        assertEquals(Platform.PC, Platform.of("PC"));
        assertEquals(Platform.PLAYSTATION, Platform.of("playstation"));
    }

    @Test
    void givenInvalidName_whenOf_thenThrows() {
        assertThrows(DomainException.class, () -> Platform.of("SEGA"));
    }

    @Test
    void givenNull_whenOf_thenThrows() {
        assertThrows(DomainException.class, () -> Platform.of(null));
    }
}
