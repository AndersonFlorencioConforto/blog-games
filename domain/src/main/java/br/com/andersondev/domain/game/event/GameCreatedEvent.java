package br.com.andersondev.domain.game.event;

import br.com.andersondev.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Emitido apos cadastro de jogo.
 */
public record GameCreatedEvent(String gameId, String title, Instant occurredOn) implements DomainEvent {

    public static GameCreatedEvent of(final String gameId, final String title) {
        return new GameCreatedEvent(gameId, title, Instant.now());
    }
}
