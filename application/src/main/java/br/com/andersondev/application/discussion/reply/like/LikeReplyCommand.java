package br.com.andersondev.application.discussion.reply.like;

/**
 * Comando para curtir uma resposta.
 */
public record LikeReplyCommand(String replyId, String userId) {

    public static LikeReplyCommand with(final String replyId, final String userId) {
        return new LikeReplyCommand(replyId, userId);
    }
}
