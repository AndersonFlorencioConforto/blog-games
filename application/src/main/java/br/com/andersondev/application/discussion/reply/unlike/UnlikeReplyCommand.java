package br.com.andersondev.application.discussion.reply.unlike;

/**
 * Comando para remover o like de uma resposta.
 */
public record UnlikeReplyCommand(String replyId, String userId) {

    public static UnlikeReplyCommand with(final String replyId, final String userId) {
        return new UnlikeReplyCommand(replyId, userId);
    }
}
