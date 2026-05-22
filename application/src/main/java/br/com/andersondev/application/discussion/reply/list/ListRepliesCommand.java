package br.com.andersondev.application.discussion.reply.list;

/**
 * Comando para listagem de respostas de uma thread.
 */
public record ListRepliesCommand(String threadId, int page, int size) {

    public static ListRepliesCommand with(final String threadId, final int page, final int size) {
        return new ListRepliesCommand(threadId, page, size);
    }
}
