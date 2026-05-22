package br.com.andersondev.infrastructure.rating.models;

import br.com.andersondev.application.rating.rate.RateGameOutput;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Resposta da avaliacao de um jogo (POST /games/{id}/ratings).
 */
public record RateGameResponse(
        String gameId,
        String userId,
        int stars,
        Instant ratedAt,
        BigDecimal newAverage,
        int totalRatings
) {

    public static RateGameResponse from(final RateGameOutput output) {
        return new RateGameResponse(
                output.gameId(),
                output.userId(),
                output.stars(),
                output.ratedAt(),
                output.newAverage(),
                output.totalRatings()
        );
    }
}
