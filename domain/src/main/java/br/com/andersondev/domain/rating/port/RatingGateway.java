package br.com.andersondev.domain.rating.port;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.rating.Rating;
import br.com.andersondev.domain.rating.RatingAggregate;
import br.com.andersondev.domain.user.UserId;

import java.util.Optional;

/**
 * Porta de saida para persistencia de avaliacoes.
 * Implementada na infrastructure (adapter JPA).
 */
public interface RatingGateway {

    Rating save(Rating rating);

    Optional<Rating> findByGameIdAndUserId(GameId gameId, UserId userId);

    /**
     * Recalcula media (AVG) e total (COUNT) das avaliacoes de um jogo.
     * Fonte de verdade usada para atualizar os campos desnormalizados de Game
     * na mesma transacao (data-strategy secao 5).
     */
    RatingAggregate aggregateByGameId(GameId gameId);
}
