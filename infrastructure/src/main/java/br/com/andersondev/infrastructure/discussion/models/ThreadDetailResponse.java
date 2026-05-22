package br.com.andersondev.infrastructure.discussion.models;

import br.com.andersondev.application.discussion.thread.get.ThreadDetailOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Response detalhada de Thread.
 */
public record ThreadDetailResponse(
        @JsonProperty("id") String id,
        @JsonProperty("game_id") String gameId,
        @JsonProperty("author_id") String authorId,
        @JsonProperty("title") String title,
        @JsonProperty("content") String content,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("reply_count") int replyCount,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {

    public static ThreadDetailResponse from(final ThreadDetailOutput output) {
        return new ThreadDetailResponse(
                output.id(),
                output.gameId(),
                output.authorId(),
                output.title(),
                output.content(),
                output.likeCount(),
                output.replyCount(),
                output.createdAt(),
                output.updatedAt()
        );
    }
}
