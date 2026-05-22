package br.com.andersondev.domain.user.event;

import br.com.andersondev.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Emitido apos cadastro bem-sucedido de usuario.
 */
public record UserRegisteredEvent(String userId, String email, Instant occurredOn) implements DomainEvent {

    public static UserRegisteredEvent of(final String userId, final String email) {
        return new UserRegisteredEvent(userId, email, Instant.now());
    }
}
