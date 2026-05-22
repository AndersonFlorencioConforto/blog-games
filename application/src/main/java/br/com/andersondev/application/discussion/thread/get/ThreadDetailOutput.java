package br.com.andersondev.application.discussion.thread.get;

import br.com.andersondev.domain.discussion.Thread;

import java.time.Instant;

/**
 * Output detalhado de Thread (inclui updatedAt).
 */
public record ThreadDetailOutput(
        String id,
        String gameId,
        String authorId,
        String title,
        String content,
        int likeCount,
        int replyCount,
        Instant createdAt,
        Instant updatedAt
) {

    public static ThreadDetailOutput from(final Thread thread) {
        return new ThreadDetailOutput(
                thread.getId().getValue(),
                thread.getGameId().getValue(),
                thread.getAuthorId().getValue(),
                thread.getTitle(),
                thread.getContent(),
                thread.getLikeCount(),
                thread.getReplyCount(),
                thread.getCreatedAt(),
                thread.getUpdatedAt()
        );
    }
}
