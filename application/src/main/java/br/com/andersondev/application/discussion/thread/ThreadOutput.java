package br.com.andersondev.application.discussion.thread;

import br.com.andersondev.domain.discussion.Thread;

import java.time.Instant;

/**
 * Output de criacao/consulta basica de Thread.
 */
public record ThreadOutput(
        String id,
        String gameId,
        String authorId,
        String title,
        String content,
        int likeCount,
        int replyCount,
        Instant createdAt
) {

    public static ThreadOutput from(final Thread thread) {
        return new ThreadOutput(
                thread.getId().getValue(),
                thread.getGameId().getValue(),
                thread.getAuthorId().getValue(),
                thread.getTitle(),
                thread.getContent(),
                thread.getLikeCount(),
                thread.getReplyCount(),
                thread.getCreatedAt()
        );
    }
}
