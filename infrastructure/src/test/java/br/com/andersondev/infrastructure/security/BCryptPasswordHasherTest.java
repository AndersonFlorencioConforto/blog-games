package br.com.andersondev.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();

    @Test
    void givenRawPassword_whenHash_thenProducesDifferentValue() {
        final var hash = hasher.hash("MinhaSenh@123");

        assertNotEquals("MinhaSenh@123", hash);
        assertTrue(hash.startsWith("$2"));
    }

    @Test
    void givenCorrectPassword_whenMatches_thenTrue() {
        final var hash = hasher.hash("MinhaSenh@123");

        assertTrue(hasher.matches("MinhaSenh@123", hash));
    }

    @Test
    void givenWrongPassword_whenMatches_thenFalse() {
        final var hash = hasher.hash("MinhaSenh@123");

        assertFalse(hasher.matches("OutraSenha@1", hash));
    }

    @Test
    void givenNulls_whenMatches_thenFalse() {
        assertFalse(hasher.matches(null, "x"));
        assertFalse(hasher.matches("x", null));
    }
}
