package br.com.andersondev.application.news.delete;

/**
 * Comando de entrada para exclusao de uma noticia.
 */
public record DeleteNewsCommand(String newsId) {

    public static DeleteNewsCommand with(final String newsId) {
        return new DeleteNewsCommand(newsId);
    }
}
