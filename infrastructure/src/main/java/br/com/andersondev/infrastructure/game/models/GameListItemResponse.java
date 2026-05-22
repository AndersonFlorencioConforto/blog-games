package br.com.andersondev.infrastructure.game.models;

import br.com.andersondev.application.game.GameListItemOutput;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Item resumido de jogo nas listagens (GET /games).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GameListItemResponse(
        String id,
        String title,
        String coverImageUrl,
        BigDecimal platformScore,
        BigDecimal userAverageRating,
        int totalRatings,
        List<String> categories,
        List<String> platforms,
        Instant createdAt
) {

    public static GameListItemResponse from(final GameListItemOutput output) {
        return new GameListItemResponse(
                output.id(),
                output.title(),
                output.coverImageUrl(),
                output.platformScore(),
                output.userAverageRating(),
                output.totalRatings(),
                output.categories(),
                output.platforms(),
                output.createdAt()
        );
    }
}
