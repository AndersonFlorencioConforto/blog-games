package br.com.andersondev.domain.game.event;

import br.com.andersondev.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Emitido apos soft delete de jogo (P-013).
 */
public record GameDeletedEvent(String gameId, Instant occurredOn) implements DomainEvent {

    public static GameDeletedEvent of(final String gameId) {
        return new GameDeletedEvent(gameId, Instant.now());
    }
}
