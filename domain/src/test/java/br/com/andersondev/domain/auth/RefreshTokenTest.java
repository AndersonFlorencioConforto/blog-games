package br.com.andersondev.domain.auth;

import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenTest {

    @Test
    void givenIssued_whenUsable_thenTrue() {
        final var token = RefreshToken.issue(
                UserId.unique(), "hash", Instant.now().plusSeconds(3600), "device");

        assertTrue(token.isUsable(Instant.now()));
        assertFalse(token.isRevoked());
        assertNotNull(token.getId());
    }

    @Test
    void givenExpired_whenUsable_thenFalse() {
        final var token = RefreshToken.issue(
                UserId.unique(), "hash", Instant.now().minusSeconds(10), "device");

        assertTrue(token.isExpired(Instant.now()));
        assertFalse(token.isUsable(Instant.now()));
    }

    @Test
    void givenRevoked_whenUsable_thenFalse() {
        final var token = RefreshToken.issue(
                UserId.unique(), "hash", Instant.now().plusSeconds(3600), "device");

        token.revoke();

        assertTrue(token.isRevoked());
        assertFalse(token.isUsable(Instant.now()));
    }

    @Test
    void givenAlreadyRevoked_whenRevokeAgain_thenKeepsFirstTimestamp() {
        final var token = RefreshToken.issue(
                UserId.unique(), "hash", Instant.now().plusSeconds(3600), "device");

        token.revoke();
        final var first = token.getRevokedAt();
        token.revoke();

        assertTrue(first == token.getRevokedAt() || first.equals(token.getRevokedAt()));
    }
}
