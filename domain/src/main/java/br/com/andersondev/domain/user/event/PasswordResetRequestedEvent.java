package br.com.andersondev.domain.user.event;

import br.com.andersondev.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Emitido apos solicitacao de recuperacao de senha.
 */
public record PasswordResetRequestedEvent(String userId, String email, Instant occurredOn) implements DomainEvent {

    public static PasswordResetRequestedEvent of(final String userId, final String email) {
        return new PasswordResetRequestedEvent(userId, email, Instant.now());
    }
}
