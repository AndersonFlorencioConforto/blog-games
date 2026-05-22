package br.com.andersondev.application.shelf.update;

import br.com.andersondev.application.shelf.ShelfItemOutput;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.ShelfStatus;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.handler.Notification;

import java.util.Objects;

/**
 * Atualiza o status de um jogo na propria estante (PUT /shelf/{gameId}).
 * Fluxo:
 *  1. Converte o status (S-01 -&gt; 422 se invalido).
 *  2. Carrega o item da propria estante (404 se nao esta na estante).
 *  3. Aplica o novo status e persiste.
 * Ownership e garantido porque o userId vem do contexto autenticado (S-04).
 */
public final class DefaultUpdateShelfStatusUseCase extends UpdateShelfStatusUseCase {

    private final ShelfItemGateway shelfItemGateway;

    public DefaultUpdateShelfStatusUseCase(final ShelfItemGateway shelfItemGateway) {
        this.shelfItemGateway = Objects.requireNonNull(shelfItemGateway);
    }

    @Override
    public ShelfItemOutput execute(final UpdateShelfStatusCommand command) {
        final var notification = Notification.create();
        final var status = notification.validate(() -> ShelfStatus.of(command.status()));
        if (notification.hasError()) {
            throw new NotificationException("Nao foi possivel atualizar o item da estante", notification);
        }

        final var userId = UserId.from(command.userId());
        final var gameId = GameId.from(command.gameId());

        final var item = this.shelfItemGateway.findByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> EntityNotFoundException.with(
                        ShelfItem.class, gameId));

        item.changeStatus(status);
        return ShelfItemOutput.from(this.shelfItemGateway.save(item));
    }
}
