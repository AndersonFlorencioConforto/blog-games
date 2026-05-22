package br.com.andersondev.infrastructure.news.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request de atualizacao de noticia.
 */
public record UpdateNewsRequest(
        @JsonProperty("title") String title,
        @JsonProperty("summary") String summary,
        @JsonProperty("content") String content,
        @JsonProperty("cover_image_url") String coverImageUrl,
        @JsonProperty("related_game_id") String relatedGameId
) {
}
