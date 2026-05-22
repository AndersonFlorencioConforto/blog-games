package br.com.andersondev.application.discussion.reply;

import br.com.andersondev.domain.discussion.Reply;

import java.time.Instant;

/**
 * Output de Reply.
 */
public record ReplyOutput(
        String id,
        String threadId,
        String authorId,
        String content,
        int likeCount,
        Instant createdAt
) {

    public static ReplyOutput from(final Reply reply) {
        return new ReplyOutput(
                reply.getId().getValue(),
                reply.getThreadId().getValue(),
                reply.getAuthorId().getValue(),
                reply.getContent(),
                reply.getLikeCount(),
                reply.getCreatedAt()
        );
    }
}
