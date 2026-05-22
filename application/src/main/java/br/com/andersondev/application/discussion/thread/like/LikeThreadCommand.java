package br.com.andersondev.application.discussion.thread.like;

/**
 * Comando para curtir uma thread.
 */
public record LikeThreadCommand(String threadId, String userId) {

    public static LikeThreadCommand with(final String threadId, final String userId) {
        return new LikeThreadCommand(threadId, userId);
    }
}
