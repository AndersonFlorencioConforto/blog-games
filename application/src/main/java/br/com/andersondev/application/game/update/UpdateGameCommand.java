package br.com.andersondev.application.game.update;

import java.math.BigDecimal;
import java.util.List;

/**
 * Comando de atualizacao de jogo (PUT /games/{id}).
 */
public record UpdateGameCommand(
        String id,
        String title,
        String description,
        String editorialReview,
        BigDecimal platformScore,
        List<String> pros,
        List<String> cons,
        String coverImageUrl,
        List<String> categories,
        List<String> platforms
) {

    public static UpdateGameCommand with(
            final String id,
            final String title,
            final String description,
            final String editorialReview,
            final BigDecimal platformScore,
            final List<String> pros,
            final List<String> cons,
            final String coverImageUrl,
            final List<String> categories,
            final List<String> platforms
    ) {
        return new UpdateGameCommand(
                id, title, description, editorialReview, platformScore,
                pros, cons, coverImageUrl, categories, platforms);
    }
}
