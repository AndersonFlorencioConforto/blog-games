package br.com.andersondev.application.discussion.thread.unlike;

/**
 * Comando para remover o like de uma thread.
 */
public record UnlikeThreadCommand(String threadId, String userId) {

    public static UnlikeThreadCommand with(final String threadId, final String userId) {
        return new UnlikeThreadCommand(threadId, userId);
    }
}
