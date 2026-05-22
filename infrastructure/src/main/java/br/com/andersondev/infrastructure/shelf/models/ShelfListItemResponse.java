package br.com.andersondev.infrastructure.shelf.models;

import br.com.andersondev.application.shelf.ShelfListItemOutput;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Item da estante na listagem publica (GET /users/{userId}/shelf):
 * dados resumidos do jogo + status + addedAt.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShelfListItemResponse(GameSummaryResponse game, String status, Instant addedAt) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GameSummaryResponse(String id, String title, String coverImageUrl) {
    }

    public static ShelfListItemResponse from(final ShelfListItemOutput output) {
        final var game = output.game();
        return new ShelfListItemResponse(
                new GameSummaryResponse(game.id(), game.title(), game.coverImageUrl()),
                output.status(),
                output.addedAt()
        );
    }
}
