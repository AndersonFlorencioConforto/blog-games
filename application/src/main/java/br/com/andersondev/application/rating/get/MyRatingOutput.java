package br.com.andersondev.application.rating.get;

import br.com.andersondev.domain.rating.Rating;

import java.time.Instant;

/**
 * Saida da avaliacao propria de um jogo (GET /games/{id}/ratings/me).
 */
public record MyRatingOutput(int stars, Instant ratedAt) {

    public static MyRatingOutput from(final Rating rating) {
        return new MyRatingOutput(rating.getStars().getValue(), rating.getRatedAt());
    }
}
