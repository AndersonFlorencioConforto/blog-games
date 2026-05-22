package br.com.andersondev.infrastructure.news.models;

import br.com.andersondev.application.news.NewsSummaryOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Response resumido de noticia para listagens.
 */
public record NewsSummaryResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("summary") String summary,
        @JsonProperty("cover_image_url") String coverImageUrl,
        @JsonProperty("author_id") String authorId,
        @JsonProperty("related_game_id") String relatedGameId,
        @JsonProperty("published_at") Instant publishedAt
) {

    public static NewsSummaryResponse from(final NewsSummaryOutput output) {
        return new NewsSummaryResponse(
                output.id(),
                output.title(),
                output.summary(),
                output.coverImageUrl(),
                output.authorId(),
                output.relatedGameId(),
                output.publishedAt()
        );
    }
}
