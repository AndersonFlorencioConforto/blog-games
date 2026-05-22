package br.com.andersondev.application.discussion.thread.create;

/**
 * Comando para criacao de uma thread de discussao.
 */
public record CreateThreadCommand(String gameId, String authorId, String title, String content) {

    public static CreateThreadCommand with(
            final String gameId,
            final String authorId,
            final String title,
            final String content
    ) {
        return new CreateThreadCommand(gameId, authorId, title, content);
    }
}
