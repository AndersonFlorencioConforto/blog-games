package br.com.andersondev.domain.rating;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.rating.event.GameRatedEvent;
import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.time.Instant;

/**
 * Raiz do agregado Rating (domain-catalog 2.3). Um usuario possui no maximo uma
 * avaliacao por jogo (unicidade garantida pela porta/constraint game_id + user_id).
 */
public class Rating extends AggregateRoot<RatingId> {

    private final GameId gameId;
    private final UserId userId;
    private Stars stars;
    private Instant ratedAt;

    private Rating(
            final RatingId id,
            final GameId gameId,
            final UserId userId,
            final Stars stars,
            final Instant ratedAt
    ) {
        super(id);
        this.gameId = gameId;
        this.userId = userId;
        this.stars = stars;
        this.ratedAt = ratedAt;
    }

    /**
     * Cria uma nova avaliacao e registra {@link GameRatedEvent} com {@code isNewRating = true}.
     */
    public static Rating newRating(final GameId gameId, final UserId userId, final Stars stars) {
        final var rating = new Rating(RatingId.unique(), gameId, userId, stars, Instant.now());
        rating.registerEvent(GameRatedEvent.of(
                gameId.getValue(), userId.getValue(), stars.getValue(), true));
        return rating;
    }

    /**
     * Reidrata uma avaliacao existente a partir da persistencia.
     */
    public static Rating with(
            final RatingId id,
            final GameId gameId,
            final UserId userId,
            final Stars stars,
            final Instant ratedAt
    ) {
        return new Rating(id, gameId, userId, stars, ratedAt);
    }

    /**
     * Atualiza a nota de uma avaliacao existente (R-03) e registra
     * {@link GameRatedEvent} com {@code isNewRating = false}.
     */
    public void changeStars(final Stars newStars) {
        this.stars = newStars;
        this.ratedAt = Instant.now();
        registerEvent(GameRatedEvent.of(
                this.gameId.getValue(), this.userId.getValue(), newStars.getValue(), false));
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new RatingValidator(this, handler).validate();
    }

    public GameId getGameId() {
        return this.gameId;
    }

    public UserId getUserId() {
        return this.userId;
    }

    public Stars getStars() {
        return this.stars;
    }

    public Instant getRatedAt() {
        return this.ratedAt;
    }
}
