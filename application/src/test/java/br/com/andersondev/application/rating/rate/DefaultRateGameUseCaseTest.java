package br.com.andersondev.application.rating.rate;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.InvalidRangeException;
import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.Platform;
import br.com.andersondev.domain.game.PlatformScore;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.rating.Rating;
import br.com.andersondev.domain.rating.RatingAggregate;
import br.com.andersondev.domain.rating.Stars;
import br.com.andersondev.domain.rating.port.RatingGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRateGameUseCaseTest {

    @Mock
    private GameGateway gameGateway;

    @Mock
    private RatingGateway ratingGateway;

    @InjectMocks
    private DefaultRateGameUseCase useCase;

    private static Game game() {
        return Game.newGame(
                "Title", "desc", null, PlatformScore.of(new BigDecimal("8.0")),
                List.of(), List.of(), null,
                EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
    }

    @Test
    void givenNewRating_whenExecute_thenSavesRatingAndUpdatesAverage() {
        final var game = game();
        final var gameId = game.getId().getValue();
        final var userId = UserId.unique().getValue();

        when(gameGateway.findByIdForUpdate(any(GameId.class))).thenReturn(Optional.of(game));
        when(ratingGateway.findByGameIdAndUserId(any(GameId.class), any(UserId.class)))
                .thenReturn(Optional.empty());
        when(ratingGateway.save(any(Rating.class))).thenAnswer(returnsFirstArg());
        when(ratingGateway.aggregateByGameId(any(GameId.class)))
                .thenReturn(RatingAggregate.of(new BigDecimal("4.00"), 1));
        when(gameGateway.save(any(Game.class))).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(RateGameCommand.with(gameId, userId, 4));

        assertEquals(4, output.stars());
        assertEquals(new BigDecimal("4.00"), output.newAverage());
        assertEquals(1, output.totalRatings());

        // novo rating salvo
        final var ratingCaptor = ArgumentCaptor.forClass(Rating.class);
        verify(ratingGateway).save(ratingCaptor.capture());
        assertEquals(4, ratingCaptor.getValue().getStars().getValue());

        // media desnormalizada aplicada ao jogo
        final var gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameGateway).save(gameCaptor.capture());
        assertEquals(new BigDecimal("4.00"), gameCaptor.getValue().getAverageUserRating());
        assertEquals(1, gameCaptor.getValue().getTotalRatings());
    }

    @Test
    void givenExistingRating_whenExecute_thenUpdatesStarsAndRecalculatesAverage() {
        final var game = game();
        game.applyRatingAggregate(new BigDecimal("2.00"), 1);
        final var gameId = game.getId();
        final var userId = UserId.unique();
        final var existing = Rating.newRating(gameId, userId, Stars.of(2));

        when(gameGateway.findByIdForUpdate(any(GameId.class))).thenReturn(Optional.of(game));
        when(ratingGateway.findByGameIdAndUserId(any(GameId.class), any(UserId.class)))
                .thenReturn(Optional.of(existing));
        when(ratingGateway.save(any(Rating.class))).thenAnswer(returnsFirstArg());
        when(ratingGateway.aggregateByGameId(any(GameId.class)))
                .thenReturn(RatingAggregate.of(new BigDecimal("5.00"), 1));
        when(gameGateway.save(any(Game.class))).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(
                RateGameCommand.with(gameId.getValue(), userId.getValue(), 5));

        assertEquals(5, output.stars());
        assertEquals(new BigDecimal("5.00"), output.newAverage());
        assertEquals(1, output.totalRatings());

        final var ratingCaptor = ArgumentCaptor.forClass(Rating.class);
        verify(ratingGateway).save(ratingCaptor.capture());
        // mesma identidade de rating (upsert, nao duplica)
        assertEquals(existing.getId(), ratingCaptor.getValue().getId());
        assertEquals(5, ratingCaptor.getValue().getStars().getValue());
    }

    @Test
    void givenStarsOutOfRange_whenExecute_thenThrowsInvalidRange() {
        assertThrows(InvalidRangeException.class,
                () -> useCase.execute(RateGameCommand.with(
                        GameId.unique().getValue(), UserId.unique().getValue(), 7)));
        verify(ratingGateway, never()).save(any());
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenUnknownGame_whenExecute_thenThrowsNotFound() {
        when(gameGateway.findByIdForUpdate(any(GameId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(RateGameCommand.with(
                        GameId.unique().getValue(), UserId.unique().getValue(), 4)));
        verify(ratingGateway, never()).save(any());
    }

    @Test
    void givenDeletedGame_whenExecute_thenThrowsNotFound() {
        final var game = game();
        game.delete();
        when(gameGateway.findByIdForUpdate(any(GameId.class))).thenReturn(Optional.of(game));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(RateGameCommand.with(
                        game.getId().getValue(), UserId.unique().getValue(), 4)));
        verify(ratingGateway, never()).save(any());
    }

    @Test
    void givenRatingGatewayFailure_whenExecute_thenPropagates() {
        final var game = game();
        when(gameGateway.findByIdForUpdate(any(GameId.class))).thenReturn(Optional.of(game));
        when(ratingGateway.findByGameIdAndUserId(any(GameId.class), any(UserId.class)))
                .thenReturn(Optional.empty());
        when(ratingGateway.save(any(Rating.class))).thenThrow(new RuntimeException("db down"));

        assertThrows(RuntimeException.class,
                () -> useCase.execute(RateGameCommand.with(
                        game.getId().getValue(), UserId.unique().getValue(), 4)));
        verify(gameGateway, never()).save(any());
    }
}
