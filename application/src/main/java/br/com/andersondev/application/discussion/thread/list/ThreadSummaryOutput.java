package br.com.andersondev.application.discussion.thread.list;

import br.com.andersondev.domain.discussion.Thread;

import java.time.Instant;

/**
 * Output resumido de Thread para listagem.
 */
public record ThreadSummaryOutput(
        String id,
        String title,
        String authorId,
        int likeCount,
        int replyCount,
        Instant createdAt
) {

    public static ThreadSummaryOutput from(final Thread thread) {
        return new ThreadSummaryOutput(
                thread.getId().getValue(),
                thread.getTitle(),
                thread.getAuthorId().getValue(),
                thread.getLikeCount(),
                thread.getReplyCount(),
                thread.getCreatedAt()
        );
    }
}
