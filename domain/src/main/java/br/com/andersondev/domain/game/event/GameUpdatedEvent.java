package br.com.andersondev.domain.game.event;

import br.com.andersondev.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Emitido apos atualizacao de jogo.
 */
public record GameUpdatedEvent(String gameId, String title, Instant occurredOn) implements DomainEvent {

    public static GameUpdatedEvent of(final String gameId, final String title) {
        return new GameUpdatedEvent(gameId, title, Instant.now());
    }
}
