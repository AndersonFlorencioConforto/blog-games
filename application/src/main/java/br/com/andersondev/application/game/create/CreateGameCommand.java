package br.com.andersondev.application.game.create;

import java.math.BigDecimal;
import java.util.List;

/**
 * Comando de cadastro de jogo (POST /games). Categorias e plataformas chegam como
 * strings e sao convertidas para os enums de dominio no caso de uso.
 */
public record CreateGameCommand(
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

    public static CreateGameCommand with(
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
        return new CreateGameCommand(
                title, description, editorialReview, platformScore,
                pros, cons, coverImageUrl, categories, platforms);
    }
}
