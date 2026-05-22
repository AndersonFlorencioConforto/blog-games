package br.com.andersondev.infrastructure.discussion.models;

import br.com.andersondev.application.discussion.thread.ThreadOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Response de Thread apos criacao.
 */
public record ThreadResponse(
        @JsonProperty("id") String id,
        @JsonProperty("game_id") String gameId,
        @JsonProperty("author_id") String authorId,
        @JsonProperty("title") String title,
        @JsonProperty("content") String content,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("reply_count") int replyCount,
        @JsonProperty("created_at") Instant createdAt
) {

    public static ThreadResponse from(final ThreadOutput output) {
        return new ThreadResponse(
                output.id(),
                output.gameId(),
                output.authorId(),
                output.title(),
                output.content(),
                output.likeCount(),
                output.replyCount(),
                output.createdAt()
        );
    }
}
