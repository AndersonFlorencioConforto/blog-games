package br.com.andersondev.application.game;

import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.Platform;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Item resumido de jogo para listagens (espelha GET /games).
 */
public record GameListItemOutput(
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

    public static GameListItemOutput from(final Game game) {
        return new GameListItemOutput(
                game.getId().getValue(),
                game.getTitle(),
                game.getCoverImageUrl(),
                game.getPlatformScore().getValue(),
                game.getAverageUserRating(),
                game.getTotalRatings(),
                game.getCategories().stream().map(Category::name).toList(),
                game.getPlatforms().stream().map(Platform::name).toList(),
                game.getCreatedAt()
        );
    }
}
