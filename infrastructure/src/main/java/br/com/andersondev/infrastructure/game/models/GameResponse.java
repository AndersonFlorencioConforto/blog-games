package br.com.andersondev.infrastructure.game.models;

import br.com.andersondev.application.game.GameOutput;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Resposta com detalhe completo de jogo (GET /games/{id}, POST /games, PUT /games/{id}).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GameResponse(
        String id,
        String title,
        String description,
        String editorialReview,
        BigDecimal platformScore,
        BigDecimal userAverageRating,
        int totalRatings,
        List<String> pros,
        List<String> cons,
        String coverImageUrl,
        List<String> categories,
        List<String> platforms,
        Instant createdAt,
        Instant updatedAt
) {

    public static GameResponse from(final GameOutput output) {
        return new GameResponse(
                output.id(),
                output.title(),
                output.description(),
                output.editorialReview(),
                output.platformScore(),
                output.userAverageRating(),
                output.totalRatings(),
                output.pros(),
                output.cons(),
                output.coverImageUrl(),
                output.categories(),
                output.platforms(),
                output.createdAt(),
                output.updatedAt()
        );
    }
}
