package br.com.andersondev.application.shelf;

import br.com.andersondev.domain.game.GameSummary;
import br.com.andersondev.domain.shelf.ShelfItem;

import java.time.Instant;

/**
 * Item da estante para listagem publica (GET /users/{userId}/shelf): traz
 * dados resumidos do jogo + status + addedAt (api-catalog secao 4).
 */
public record ShelfListItemOutput(GameSummaryView game, String status, Instant addedAt) {

    public record GameSummaryView(String id, String title, String coverImageUrl) {
    }

    public static ShelfListItemOutput of(final ShelfItem item, final GameSummary game) {
        return new ShelfListItemOutput(
                new GameSummaryView(game.id(), game.title(), game.coverImageUrl()),
                item.getStatus().name(),
                item.getAddedAt()
        );
    }
}
