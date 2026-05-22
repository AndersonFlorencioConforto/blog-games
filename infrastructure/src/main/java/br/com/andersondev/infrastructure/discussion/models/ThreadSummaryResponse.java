package br.com.andersondev.infrastructure.discussion.models;

import br.com.andersondev.application.discussion.thread.list.ThreadSummaryOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Response resumido de Thread para listagem.
 */
public record ThreadSummaryResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("author_id") String authorId,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("reply_count") int replyCount,
        @JsonProperty("created_at") Instant createdAt
) {

    public static ThreadSummaryResponse from(final ThreadSummaryOutput output) {
        return new ThreadSummaryResponse(
                output.id(),
                output.title(),
                output.authorId(),
                output.likeCount(),
                output.replyCount(),
                output.createdAt()
        );
    }
}
