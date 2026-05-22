package br.com.andersondev.domain.shelf;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.time.Instant;

/**
 * Raiz do agregado ShelfItem (domain-catalog 2.4). Representa um jogo na estante
 * de um usuario com um {@link ShelfStatus}.
 *
 * Invariantes:
 *  - par (userId, gameId) unico na estante (S-02, garantido pela UNIQUE no banco e pelo caso de uso);
 *  - status obrigatorio (S-01).
 */
public class ShelfItem extends AggregateRoot<ShelfItemId> {

    private final UserId userId;
    private final GameId gameId;
    private ShelfStatus status;
    private final Instant addedAt;
    private Instant updatedAt;

    private ShelfItem(
            final ShelfItemId id,
            final UserId userId,
            final GameId gameId,
            final ShelfStatus status,
            final Instant addedAt,
            final Instant updatedAt
    ) {
        super(id);
        this.userId = userId;
        this.gameId = gameId;
        this.status = status;
        this.addedAt = addedAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Adiciona um jogo a estante do usuario com um status.
     */
    public static ShelfItem newItem(final UserId userId, final GameId gameId, final ShelfStatus status) {
        final var now = Instant.now();
        return new ShelfItem(ShelfItemId.unique(), userId, gameId, status, now, now);
    }

    /**
     * Reidrata um item existente a partir da persistencia.
     */
    public static ShelfItem with(
            final ShelfItemId id,
            final UserId userId,
            final GameId gameId,
            final ShelfStatus status,
            final Instant addedAt,
            final Instant updatedAt
    ) {
        return new ShelfItem(id, userId, gameId, status, addedAt, updatedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new ShelfItemValidator(this, handler).validate();
    }

    /**
     * Atualiza o status do item (PUT /shelf/{gameId}).
     */
    public void changeStatus(final ShelfStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

    public UserId getUserId() {
        return this.userId;
    }

    public GameId getGameId() {
        return this.gameId;
    }

    public ShelfStatus getStatus() {
        return this.status;
    }

    public Instant getAddedAt() {
        return this.addedAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }
}
