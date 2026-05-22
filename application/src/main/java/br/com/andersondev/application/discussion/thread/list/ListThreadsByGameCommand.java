package br.com.andersondev.application.discussion.thread.list;

/**
 * Comando para listagem de threads por jogo.
 */
public record ListThreadsByGameCommand(String gameId, int page, int size) {

    public static ListThreadsByGameCommand with(final String gameId, final int page, final int size) {
        return new ListThreadsByGameCommand(gameId, page, size);
    }
}
