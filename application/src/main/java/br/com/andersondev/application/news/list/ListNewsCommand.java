package br.com.andersondev.application.news.list;

/**
 * Comando de entrada para listagem paginada de noticias.
 * relatedGameId e opcional; se nulo lista todas as noticias.
 */
public record ListNewsCommand(
        String relatedGameId,
        int page,
        int size
) {

    public static ListNewsCommand with(final String relatedGameId, final int page, final int size) {
        return new ListNewsCommand(relatedGameId, page, size);
    }
}
