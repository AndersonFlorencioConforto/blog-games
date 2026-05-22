package br.com.andersondev.application.game.list;

/**
 * Comando de listagem paginada de jogos (GET /games).
 * Filtros {@code category} e {@code platform} sao opcionais (nullable/blank ignorados).
 */
public record ListGamesCommand(
        int page,
        int size,
        String sort,
        String direction,
        String category,
        String platform
) {

    public static ListGamesCommand with(
            final int page,
            final int size,
            final String sort,
            final String direction,
            final String category,
            final String platform
    ) {
        return new ListGamesCommand(page, size, sort, direction, category, platform);
    }
}
