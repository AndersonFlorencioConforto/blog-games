package br.com.andersondev.application.news.create;

/**
 * Comando de entrada para criacao de uma noticia.
 */
public record CreateNewsCommand(
        String title,
        String summary,
        String content,
        String coverImageUrl,
        String authorId,
        String relatedGameId
) {

    public static CreateNewsCommand with(
            final String title,
            final String summary,
            final String content,
            final String coverImageUrl,
            final String authorId,
            final String relatedGameId
    ) {
        return new CreateNewsCommand(title, summary, content, coverImageUrl, authorId, relatedGameId);
    }
}
