package br.com.andersondev.application.shelf;

import br.com.andersondev.domain.shelf.ShelfItem;

import java.time.Instant;

/**
 * Saida de uma operacao de escrita na estante (POST /shelf, PUT /shelf/{gameId}).
 * Espelha {@code gameId, status, addedAt} (api-catalog secao 4).
 */
public record ShelfItemOutput(String gameId, String status, Instant addedAt) {

    public static ShelfItemOutput from(final ShelfItem item) {
        return new ShelfItemOutput(
                item.getGameId().getValue(),
                item.getStatus().name(),
                item.getAddedAt()
        );
    }
}
