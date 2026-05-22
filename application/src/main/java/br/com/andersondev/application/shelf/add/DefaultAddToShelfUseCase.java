package br.com.andersondev.application.shelf.add;

import br.com.andersondev.application.shelf.ShelfItemOutput;
import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.ShelfStatus;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.handler.Notification;

import java.util.Objects;

/**
 * Adiciona um jogo a propria estante (POST /shelf). S-01..S-03.
 * Fluxo:
 *  1. Converte o status (S-01 -&gt; 422 se invalido).
 *  2. Valida que o jogo existe e nao esta deletado (S-03 -&gt; 404).
 *  3. Rejeita duplicidade na estante (S-02 -&gt; 409; orienta usar PUT).
 *  4. Persiste o item.
 */
public final class DefaultAddToShelfUseCase extends AddToShelfUseCase {

    private final ShelfItemGateway shelfItemGateway;
    private final GameGateway gameGateway;

    public DefaultAddToShelfUseCase(final ShelfItemGateway shelfItemGateway, final GameGateway gameGateway) {
        this.shelfItemGateway = Objects.requireNonNull(shelfItemGateway);
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public ShelfItemOutput execute(final AddToShelfCommand command) {
        final var notification = Notification.create();
        final var status = notification.validate(() -> ShelfStatus.of(command.status()));
        if (notification.hasError()) {
            throw new NotificationException("Nao foi possivel adicionar o jogo a estante", notification);
        }

        final var userId = UserId.from(command.userId());
        final var gameId = GameId.from(command.gameId());

        final var gameExists = this.gameGateway.findById(gameId)
                .filter(game -> !game.isDeleted())
                .isPresent();
        if (!gameExists) {
            throw EntityNotFoundException.with(Game.class, gameId);
        }

        if (this.shelfItemGateway.existsByUserIdAndGameId(userId, gameId)) {
            throw DuplicateEntityException.with("Jogo ja esta na estante (use PUT para atualizar o status)");
        }

        final var item = ShelfItem.newItem(userId, gameId, status);
        return ShelfItemOutput.from(this.shelfItemGateway.save(item));
    }
}
