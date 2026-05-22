package br.com.andersondev.application.rating.rate;

import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.rating.Rating;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Saida da avaliacao de um jogo (espelha POST /games/{id}/ratings), incluindo a
 * nova media e o total apos a atualizacao desnormalizada.
 */
public record RateGameOutput(
        String gameId,
        String userId,
        int stars,
        Instant ratedAt,
        BigDecimal newAverage,
        int totalRatings
) {

    public static RateGameOutput of(final Rating rating, final Game game) {
        return new RateGameOutput(
                rating.getGameId().getValue(),
                rating.getUserId().getValue(),
                rating.getStars().getValue(),
                rating.getRatedAt(),
                game.getAverageUserRating(),
                game.getTotalRatings()
        );
    }
}
