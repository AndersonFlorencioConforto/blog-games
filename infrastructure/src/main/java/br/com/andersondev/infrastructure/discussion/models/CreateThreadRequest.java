package br.com.andersondev.infrastructure.discussion.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Request de criacao de thread.
 */
public record CreateThreadRequest(
        @NotBlank @JsonProperty("title") String title,
        @NotBlank @JsonProperty("content") String content
) {
}
