package br.com.andersondev.infrastructure.discussion.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Request de adicao de resposta.
 */
public record AddReplyRequest(
        @NotBlank @JsonProperty("content") String content
) {
}
