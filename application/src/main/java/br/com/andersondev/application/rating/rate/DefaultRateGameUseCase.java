package br.com.andersondev.application.rating.rate;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.rating.Rating;
import br.com.andersondev.domain.rating.Stars;
import br.com.andersondev.domain.rating.port.RatingGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Avaliacao de jogo (R-01..R-04). Executado em transacao unica (RateGameUseCase
 * em transaction-boundaries).
 *
 * Fluxo:
 *  1. Valida stars (VO, R-01 -&gt; 422).
 *  2. Carrega o jogo com lock pessimista (SELECT FOR UPDATE) para serializar a
 *     atualizacao da media (R-04 / data-strategy secao 5). 404 se inexistente/deletado (R-02).
 *  3. Upsert da avaliacao: cria nova ou atualiza a existente do mesmo usuario (R-03).
 *  4. Recalcula media (AVG) e total (COUNT) das avaliacoes e aplica ao jogo na
 *     mesma transacao, persistindo os campos desnormalizados.
 */
public final class DefaultRateGameUseCase extends RateGameUseCase {

    private final GameGateway gameGateway;
    private final RatingGateway ratingGateway;

    public DefaultRateGameUseCase(final GameGateway gameGateway, final RatingGateway ratingGateway) {
        this.gameGateway = Objects.requireNonNull(gameGateway);
        this.ratingGateway = Objects.requireNonNull(ratingGateway);
    }

    @Override
    public RateGameOutput execute(final RateGameCommand command) {
        final var stars = Stars.of(command.stars());
        final var gameId = GameId.from(command.gameId());
        final var userId = UserId.from(command.userId());

        // Lock pessimista no jogo para serializar a atualizacao da media.
        final var game = this.gameGateway.findByIdForUpdate(gameId)
                .filter(g -> !g.isDeleted())
                .orElseThrow(() -> EntityNotFoundException.with(Game.class, gameId));

        final var existing = this.ratingGateway.findByGameIdAndUserId(gameId, userId);

        final Rating rating;
        if (existing.isPresent()) {
            rating = existing.get();
            rating.changeStars(stars);
        } else {
            rating = Rating.newRating(gameId, userId, stars);
        }
        final var savedRating = this.ratingGateway.save(rating);

        final var aggregate = this.ratingGateway.aggregateByGameId(gameId);
        game.applyRatingAggregate(aggregate.average(), aggregate.total());
        final var savedGame = this.gameGateway.save(game);

        return RateGameOutput.of(savedRating, savedGame);
    }
}
