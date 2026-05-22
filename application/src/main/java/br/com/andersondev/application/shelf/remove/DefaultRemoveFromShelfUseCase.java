package br.com.andersondev.application.shelf.remove;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Remove um jogo da propria estante (DELETE /shelf/{gameId}).
 * 404 se o jogo nao esta na estante (api-catalog secao 4). Ownership e garantido
 * porque o userId vem do contexto autenticado.
 */
public final class DefaultRemoveFromShelfUseCase extends RemoveFromShelfUseCase {

    private final ShelfItemGateway shelfItemGateway;

    public DefaultRemoveFromShelfUseCase(final ShelfItemGateway shelfItemGateway) {
        this.shelfItemGateway = Objects.requireNonNull(shelfItemGateway);
    }

    @Override
    public void execute(final RemoveFromShelfCommand command) {
        final var userId = UserId.from(command.userId());
        final var gameId = GameId.from(command.gameId());

        if (!this.shelfItemGateway.existsByUserIdAndGameId(userId, gameId)) {
            throw EntityNotFoundException.with(ShelfItem.class, gameId);
        }
        this.shelfItemGateway.deleteByUserIdAndGameId(userId, gameId);
    }
}
