package br.com.andersondev.domain.user;

import br.com.andersondev.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailTest {

    @Test
    void givenValidEmail_whenOf_thenNormalizesToLowercase() {
        final var email = Email.of("Anderson@Email.COM");

        assertEquals("anderson@email.com", email.getValue());
    }

    @Test
    void givenEmailWithSpaces_whenOf_thenTrims() {
        final var email = Email.of("  user@email.com  ");

        assertEquals("user@email.com", email.getValue());
    }

    @Test
    void givenNullEmail_whenOf_thenThrows() {
        final var ex = assertThrows(DomainException.class, () -> Email.of(null));

        assertTrue(ex.getMessage().contains("obrigatorio"));
    }

    @Test
    void givenBlankEmail_whenOf_thenThrows() {
        assertThrows(DomainException.class, () -> Email.of("   "));
    }

    @Test
    void givenInvalidFormat_whenOf_thenThrows() {
        assertThrows(DomainException.class, () -> Email.of("not-an-email"));
        assertThrows(DomainException.class, () -> Email.of("missing@domain"));
        assertThrows(DomainException.class, () -> Email.of("@no-local.com"));
    }

    @Test
    void givenSameValueDifferentCase_whenEquals_thenAreEqual() {
        assertEquals(Email.of("a@b.com"), Email.of("A@B.COM"));
        assertEquals(Email.of("a@b.com").hashCode(), Email.of("A@B.COM").hashCode());
    }

    @Test
    void givenDifferentEmails_whenEquals_thenNotEqual() {
        assertNotEquals(Email.of("a@b.com"), Email.of("c@d.com"));
    }
}
