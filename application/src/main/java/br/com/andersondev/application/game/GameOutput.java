package br.com.andersondev.application.game;

import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.Platform;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Saida com o detalhe completo de um jogo (espelha GET /games/{id}).
 */
public record GameOutput(
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

    public static GameOutput from(final Game game) {
        return new GameOutput(
                game.getId().getValue(),
                game.getTitle(),
                game.getDescription(),
                game.getEditorialReview(),
                game.getPlatformScore().getValue(),
                game.getAverageUserRating(),
                game.getTotalRatings(),
                List.copyOf(game.getPros()),
                List.copyOf(game.getCons()),
                game.getCoverImageUrl(),
                game.getCategories().stream().map(Category::name).toList(),
                game.getPlatforms().stream().map(Platform::name).toList(),
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }
}
