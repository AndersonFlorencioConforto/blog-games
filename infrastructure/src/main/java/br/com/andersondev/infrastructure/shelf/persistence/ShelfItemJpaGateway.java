package br.com.andersondev.infrastructure.shelf.persistence;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.ShelfStatus;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ShelfItemJpaGateway implements ShelfItemGateway {

    private final ShelfItemRepository repository;

    public ShelfItemJpaGateway(final ShelfItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public ShelfItem save(final ShelfItem shelfItem) {
        return this.repository.save(ShelfItemJpaEntity.from(shelfItem)).toAggregate();
    }

    @Override
    public Optional<ShelfItem> findByUserIdAndGameId(final UserId userId, final GameId gameId) {
        return this.repository.findByUserIdAndGameId(userId.getValue(), gameId.getValue())
                .map(ShelfItemJpaEntity::toAggregate);
    }

    @Override
    public boolean existsByUserIdAndGameId(final UserId userId, final GameId gameId) {
        return this.repository.existsByUserIdAndGameId(userId.getValue(), gameId.getValue());
    }

    @Override
    public void deleteByUserIdAndGameId(final UserId userId, final GameId gameId) {
        this.repository.deleteByUserIdAndGameId(userId.getValue(), gameId.getValue());
    }

    @Override
    public Pagination<ShelfItem> findByUserId(
            final UserId userId, final ShelfStatus status, final int page, final int size) {
        final var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "addedAt"));
        final Page<ShelfItemJpaEntity> result = status == null
                ? this.repository.findByUserId(userId.getValue(), pageable)
                : this.repository.findByUserIdAndStatus(userId.getValue(), status, pageable);
        final var items = result.getContent().stream().map(ShelfItemJpaEntity::toAggregate).toList();
        return new Pagination<>(result.getNumber(), result.getSize(), result.getTotalElements(), items);
    }
}
