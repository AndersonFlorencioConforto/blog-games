package br.com.andersondev.domain.rating;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.rating.event.GameRatedEvent;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RatingTest {

    private final GameId gameId = GameId.unique();
    private final UserId userId = UserId.unique();

    @Test
    void givenValidData_whenNewRating_thenRegistersNewRatingEvent() {
        final var rating = Rating.newRating(gameId, userId, Stars.of(4));

        assertEquals(gameId, rating.getGameId());
        assertEquals(userId, rating.getUserId());
        assertEquals(4, rating.getStars().getValue());
        assertEquals(1, rating.getDomainEvents().size());
        final var event = rating.getDomainEvents().getFirst();
        assertInstanceOf(GameRatedEvent.class, event);
        assertTrue(((GameRatedEvent) event).isNewRating());
    }

    @Test
    void givenExistingRating_whenChangeStars_thenUpdatesAndRegistersUpdateEvent() {
        final var rating = Rating.newRating(gameId, userId, Stars.of(2));
        rating.clearEvents();

        rating.changeStars(Stars.of(5));

        assertEquals(5, rating.getStars().getValue());
        final var event = rating.getDomainEvents().getFirst();
        assertInstanceOf(GameRatedEvent.class, event);
        assertFalse(((GameRatedEvent) event).isNewRating());
    }
}
