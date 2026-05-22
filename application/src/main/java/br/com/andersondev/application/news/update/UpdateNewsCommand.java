package br.com.andersondev.application.news.update;

/**
 * Comando de entrada para atualizacao de uma noticia.
 */
public record UpdateNewsCommand(
        String newsId,
        String title,
        String summary,
        String content,
        String coverImageUrl,
        String relatedGameId
) {

    public static UpdateNewsCommand with(
            final String newsId,
            final String title,
            final String summary,
            final String content,
            final String coverImageUrl,
            final String relatedGameId
    ) {
        return new UpdateNewsCommand(newsId, title, summary, content, coverImageUrl, relatedGameId);
    }
}
