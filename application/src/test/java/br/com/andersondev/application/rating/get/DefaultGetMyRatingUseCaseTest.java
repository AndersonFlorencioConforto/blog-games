package br.com.andersondev.application.rating.get;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.rating.Rating;
import br.com.andersondev.domain.rating.Stars;
import br.com.andersondev.domain.rating.port.RatingGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultGetMyRatingUseCaseTest {

    @Mock
    private RatingGateway ratingGateway;

    @InjectMocks
    private DefaultGetMyRatingUseCase useCase;

    @Test
    void givenExistingRating_whenExecute_thenReturnsIt() {
        final var gameId = GameId.unique();
        final var userId = UserId.unique();
        final var rating = Rating.newRating(gameId, userId, Stars.of(4));
        when(ratingGateway.findByGameIdAndUserId(any(GameId.class), any(UserId.class)))
                .thenReturn(Optional.of(rating));

        final var output = useCase.execute(
                GetMyRatingQuery.with(gameId.getValue(), userId.getValue()));

        assertEquals(4, output.stars());
    }

    @Test
    void givenNoRating_whenExecute_thenThrowsNotFound() {
        when(ratingGateway.findByGameIdAndUserId(any(GameId.class), any(UserId.class)))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(GetMyRatingQuery.with(
                        GameId.unique().getValue(), UserId.unique().getValue())));
    }
}
