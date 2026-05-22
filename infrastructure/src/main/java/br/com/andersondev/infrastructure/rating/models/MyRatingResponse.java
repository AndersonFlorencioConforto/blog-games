package br.com.andersondev.infrastructure.rating.models;

import br.com.andersondev.application.rating.get.MyRatingOutput;

import java.time.Instant;

/**
 * Resposta da avaliacao propria de um jogo (GET /games/{id}/ratings/me).
 */
public record MyRatingResponse(int stars, Instant ratedAt) {

    public static MyRatingResponse from(final MyRatingOutput output) {
        return new MyRatingResponse(output.stars(), output.ratedAt());
    }
}
