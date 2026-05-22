package br.com.andersondev.infrastructure.discussion.models;

import br.com.andersondev.application.discussion.reply.ReplyOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Response de Reply.
 */
public record ReplyResponse(
        @JsonProperty("id") String id,
        @JsonProperty("thread_id") String threadId,
        @JsonProperty("author_id") String authorId,
        @JsonProperty("content") String content,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("created_at") Instant createdAt
) {

    public static ReplyResponse from(final ReplyOutput output) {
        return new ReplyResponse(
                output.id(),
                output.threadId(),
                output.authorId(),
                output.content(),
                output.likeCount(),
                output.createdAt()
        );
    }
}
