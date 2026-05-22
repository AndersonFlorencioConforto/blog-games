package br.com.andersondev.infrastructure.rating.persistence;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.rating.Rating;
import br.com.andersondev.domain.rating.RatingAggregate;
import br.com.andersondev.domain.rating.port.RatingGateway;
import br.com.andersondev.domain.user.UserId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class RatingJpaGateway implements RatingGateway {

    private final RatingRepository repository;

    public RatingJpaGateway(final RatingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Rating save(final Rating rating) {
        return this.repository.save(RatingJpaEntity.from(rating)).toAggregate();
    }

    @Override
    public Optional<Rating> findByGameIdAndUserId(final GameId gameId, final UserId userId) {
        return this.repository.findByGameIdAndUserId(gameId.getValue(), userId.getValue())
                .map(RatingJpaEntity::toAggregate);
    }

    @Override
    public RatingAggregate aggregateByGameId(final GameId gameId) {
        final var projection = this.repository.aggregateByGameId(gameId.getValue());
        if (projection == null || projection.total() == 0 || projection.average() == null) {
            return RatingAggregate.empty();
        }
        final var average = BigDecimal.valueOf(projection.average());
        return RatingAggregate.of(average, (int) projection.total());
    }
}
