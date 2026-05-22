package br.com.andersondev.domain.game;

/**
 * Parametros de busca paginada do catalogo de jogos.
 * Filtros {@code category} e {@code platform} sao opcionais (nullable).
 * Jogos com soft delete sao sempre excluidos pelo gateway (G-09).
 */
public record GameSearchQuery(
        int page,
        int size,
        String sort,
        String direction,
        Category category,
        Platform platform
) {

    public static GameSearchQuery with(
            final int page,
            final int size,
            final String sort,
            final String direction,
            final Category category,
            final Platform platform
    ) {
        return new GameSearchQuery(page, size, sort, direction, category, platform);
    }
}
