package br.com.andersondev.application.game.get;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.Platform;
import br.com.andersondev.domain.game.PlatformScore;
import br.com.andersondev.domain.game.port.GameGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultGetGameByIdUseCaseTest {

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultGetGameByIdUseCase useCase;

    private static Game game() {
        return Game.newGame(
                "Title", "desc", null, PlatformScore.of(new BigDecimal("7.0")),
                List.of(), List.of(), null,
                EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
    }

    @Test
    void givenExistingGame_whenExecute_thenReturnsDetail() {
        final var game = game();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));

        final var output = useCase.execute(game.getId().getValue());

        assertEquals("Title", output.title());
        assertEquals(new BigDecimal("7.00"), output.platformScore());
    }

    @Test
    void givenUnknownGame_whenExecute_thenThrowsNotFound() {
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(GameId.unique().getValue()));
    }

    @Test
    void givenDeletedGame_whenExecute_thenThrowsNotFound() {
        final var game = game();
        game.delete();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(game.getId().getValue()));
    }
}
