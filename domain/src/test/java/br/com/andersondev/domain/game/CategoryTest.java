package br.com.andersondev.domain.game;

import br.com.andersondev.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    @Test
    void givenValidName_whenOf_thenReturnsEnum() {
        assertEquals(Category.RPG, Category.of("RPG"));
        assertEquals(Category.ACAO, Category.of("acao"));
    }

    @Test
    void givenInvalidName_whenOf_thenThrows() {
        assertThrows(DomainException.class, () -> Category.of("FPS"));
    }

    @Test
    void givenBlank_whenOf_thenThrows() {
        assertThrows(DomainException.class, () -> Category.of(" "));
    }
}
