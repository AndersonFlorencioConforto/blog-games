package br.com.andersondev.infrastructure.shelf.models;

import br.com.andersondev.application.shelf.ShelfItemOutput;

import java.time.Instant;

/**
 * Resposta de uma operacao de escrita na estante (POST /shelf, PUT /shelf/{gameId}).
 */
public record ShelfItemResponse(String gameId, String status, Instant addedAt) {

    public static ShelfItemResponse from(final ShelfItemOutput output) {
        return new ShelfItemResponse(output.gameId(), output.status(), output.addedAt());
    }
}
