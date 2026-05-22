package br.com.andersondev.application.rating.get;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.rating.Rating;
import br.com.andersondev.domain.rating.port.RatingGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Retorna a avaliacao do usuario autenticado para um jogo. 404 se o usuario nao avaliou.
 */
public final class DefaultGetMyRatingUseCase extends GetMyRatingUseCase {

    private final RatingGateway ratingGateway;

    public DefaultGetMyRatingUseCase(final RatingGateway ratingGateway) {
        this.ratingGateway = Objects.requireNonNull(ratingGateway);
    }

    @Override
    public MyRatingOutput execute(final GetMyRatingQuery query) {
        final var gameId = GameId.from(query.gameId());
        final var userId = UserId.from(query.userId());
        return this.ratingGateway.findByGameIdAndUserId(gameId, userId)
                .map(MyRatingOutput::from)
                .orElseThrow(() -> EntityNotFoundException.with(
                        "Avaliacao nao encontrada para o jogo " + gameId.getValue()));
    }
}
