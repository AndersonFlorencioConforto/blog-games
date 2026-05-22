package br.com.andersondev.application.news.get;

/**
 * Comando de entrada para consulta de uma noticia por ID.
 */
public record GetNewsByIdCommand(String newsId) {

    public static GetNewsByIdCommand with(final String newsId) {
        return new GetNewsByIdCommand(newsId);
    }
}
