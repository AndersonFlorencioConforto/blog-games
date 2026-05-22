package br.com.andersondev.infrastructure.news.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request de criacao de noticia.
 * authorId e extraido do principal autenticado no controller; nao faz parte do corpo da requisicao.
 */
public record CreateNewsRequest(
        @JsonProperty("title") String title,
        @JsonProperty("summary") String summary,
        @JsonProperty("content") String content,
        @JsonProperty("cover_image_url") String coverImageUrl,
        @JsonProperty("related_game_id") String relatedGameId
) {
}
