package br.com.andersondev.domain.auth;

import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordResetTokenTest {

    @Test
    void givenIssued_whenNotExpiredNotUsed_thenValid() {
        final var token = PasswordResetToken.issue(
                UserId.unique(), "hash", Instant.now().plusSeconds(3600));

        assertFalse(token.isExpired(Instant.now()));
        assertFalse(token.isUsed());
        assertNotNull(token.getId());
    }

    @Test
    void givenExpired_whenIsExpired_thenTrue() {
        final var token = PasswordResetToken.issue(
                UserId.unique(), "hash", Instant.now().minusSeconds(10));

        assertTrue(token.isExpired(Instant.now()));
    }

    @Test
    void givenMarkedUsed_whenIsUsed_thenTrue() {
        final var token = PasswordResetToken.issue(
                UserId.unique(), "hash", Instant.now().plusSeconds(3600));

        token.markUsed();

        assertTrue(token.isUsed());
        assertNotNull(token.getUsedAt());
    }
}
