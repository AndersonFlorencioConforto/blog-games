package br.com.andersondev.application.game.delete;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.Platform;
import br.com.andersondev.domain.game.PlatformScore;
import br.com.andersondev.domain.game.port.GameGateway;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultDeleteGameUseCaseTest {

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultDeleteGameUseCase useCase;

    private static Game game() {
        return Game.newGame(
                "Title", "desc", null, PlatformScore.of(BigDecimal.ONE),
                List.of(), List.of(), null,
                EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
    }

    @Test
    void givenExistingGame_whenExecute_thenSoftDeletes() {
        final var game = game();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));
        when(gameGateway.save(any(Game.class))).thenAnswer(returnsFirstArg());

        useCase.execute(DeleteGameCommand.with(game.getId().getValue()));

        final var captor = ArgumentCaptor.forClass(Game.class);
        verify(gameGateway).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void givenUnknownGame_whenExecute_thenThrowsNotFound() {
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(DeleteGameCommand.with(GameId.unique().getValue())));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenAlreadyDeletedGame_whenExecute_thenThrowsNotFound() {
        final var game = game();
        game.delete();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(DeleteGameCommand.with(game.getId().getValue())));
        verify(gameGateway, never()).save(any());
    }
}
