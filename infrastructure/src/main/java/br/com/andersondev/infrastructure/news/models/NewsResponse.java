package br.com.andersondev.infrastructure.news.models;

import br.com.andersondev.application.news.NewsOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Response completo de noticia.
 */
public record NewsResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("summary") String summary,
        @JsonProperty("content") String content,
        @JsonProperty("cover_image_url") String coverImageUrl,
        @JsonProperty("author_id") String authorId,
        @JsonProperty("related_game_id") String relatedGameId,
        @JsonProperty("published_at") Instant publishedAt,
        @JsonProperty("updated_at") Instant updatedAt
) {

    public static NewsResponse from(final NewsOutput output) {
        return new NewsResponse(
                output.id(),
                output.title(),
                output.summary(),
                output.content(),
                output.coverImageUrl(),
                output.authorId(),
                output.relatedGameId(),
                output.publishedAt(),
                output.updatedAt()
        );
    }
}
