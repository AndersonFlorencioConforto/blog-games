package br.com.andersondev.application.discussion.reply.add;

/**
 * Comando para adicionar uma resposta a uma thread.
 */
public record AddReplyCommand(String threadId, String authorId, String content) {

    public static AddReplyCommand with(final String threadId, final String authorId, final String content) {
        return new AddReplyCommand(threadId, authorId, content);
    }
}
