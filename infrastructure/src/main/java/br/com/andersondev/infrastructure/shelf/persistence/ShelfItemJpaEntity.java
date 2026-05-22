package br.com.andersondev.infrastructure.shelf.persistence;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.ShelfItemId;
import br.com.andersondev.domain.shelf.ShelfStatus;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * Entidade JPA do agregado ShelfItem. Unicidade (user_id, game_id) garante um
 * item por par usuario+jogo (S-02). IDs como VARCHAR(36).
 */
@Entity(name = "ShelfItem")
@Table(name = "shelf_items", uniqueConstraints = @UniqueConstraint(
        name = "uk_shelf_items_user_game", columnNames = {"user_id", "game_id"}))
public class ShelfItemJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "game_id", nullable = false, length = 36)
    private String gameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ShelfStatus status;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ShelfItemJpaEntity() {
    }

    public static ShelfItemJpaEntity from(final ShelfItem item) {
        final var entity = new ShelfItemJpaEntity();
        entity.id = item.getId().getValue();
        entity.userId = item.getUserId().getValue();
        entity.gameId = item.getGameId().getValue();
        entity.status = item.getStatus();
        entity.addedAt = item.getAddedAt();
        entity.updatedAt = item.getUpdatedAt();
        return entity;
    }

    public ShelfItem toAggregate() {
        return ShelfItem.with(
                ShelfItemId.from(this.id),
                UserId.from(this.userId),
                GameId.from(this.gameId),
                this.status,
                this.addedAt,
                this.updatedAt
        );
    }
}
