package br.com.andersondev.infrastructure.game.persistence;

import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.GameSearchQuery;
import br.com.andersondev.domain.game.GameSummary;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GameJpaGateway implements GameGateway {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "createdAt", "title", "platformScore", "averageUserRating", "totalRatings", "updatedAt");
    private static final String DEFAULT_SORT = "createdAt";

    private final GameRepository repository;

    public GameJpaGateway(final GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public Game save(final Game game) {
        return this.repository.save(GameJpaEntity.from(game)).toAggregate();
    }

    @Override
    public Optional<Game> findById(final GameId id) {
        return this.repository.findById(id.getValue()).map(GameJpaEntity::toAggregate);
    }

    @Override
    public Optional<Game> findByIdForUpdate(final GameId id) {
        return this.repository.findByIdForUpdate(id.getValue()).map(GameJpaEntity::toAggregate);
    }

    @Override
    public boolean existsByTitle(final String title) {
        return this.repository.existsByTitle(title);
    }

    @Override
    public boolean existsByTitleAndIdNot(final String title, final GameId id) {
        return this.repository.existsByTitleAndIdNot(title, id.getValue());
    }

    @Override
    public Pagination<Game> findAll(final GameSearchQuery query) {
        final var pageable = PageRequest.of(query.page(), query.size(), buildSort(query));
        final var category = query.category();
        final var platform = query.platform();

        final Page<GameJpaEntity> page;
        if (category != null && platform != null) {
            page = this.repository.findActiveByCategoryAndPlatform(category, platform, pageable);
        } else if (category != null) {
            page = this.repository.findActiveByCategory(category, pageable);
        } else if (platform != null) {
            page = this.repository.findActiveByPlatform(platform, pageable);
        } else {
            page = this.repository.findAllActive(pageable);
        }

        final var items = page.getContent().stream().map(GameJpaEntity::toAggregate).toList();
        return new Pagination<>(page.getNumber(), page.getSize(), page.getTotalElements(), items);
    }

    @Override
    public Map<String, GameSummary> findSummariesByIds(final List<String> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return Map.of();
        }
        return this.repository.findSummariesByIds(gameIds).stream()
                .collect(Collectors.toMap(GameSummary::id, Function.identity(), (a, b) -> a));
    }

    private static Sort buildSort(final GameSearchQuery query) {
        final var property = ALLOWED_SORTS.contains(query.sort()) ? query.sort() : DEFAULT_SORT;
        final var direction = "asc".equalsIgnoreCase(query.direction())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }
}
