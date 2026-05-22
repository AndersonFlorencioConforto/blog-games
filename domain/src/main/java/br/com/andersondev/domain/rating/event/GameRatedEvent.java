package br.com.andersondev.domain.rating.event;

import br.com.andersondev.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Emitido apos um usuario avaliar um jogo (criacao ou atualizacao da avaliacao).
 * {@code isNewRating} distingue insercao de atualizacao.
 */
public record GameRatedEvent(
        String gameId,
        String userId,
        int stars,
        boolean isNewRating,
        Instant occurredOn
) implements DomainEvent {

    public static GameRatedEvent of(
            final String gameId,
            final String userId,
            final int stars,
            final boolean isNewRating
    ) {
        return new GameRatedEvent(gameId, userId, stars, isNewRating, Instant.now());
    }
}
