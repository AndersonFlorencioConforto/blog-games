package br.com.andersondev.application.game.update;

import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUpdateGameUseCaseTest {

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultUpdateGameUseCase useCase;

    private static Game existingGame() {
        return Game.newGame(
                "Old Title", "old desc", null, PlatformScore.of(new BigDecimal("5.0")),
                List.of(), List.of(), null,
                EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
    }

    private static UpdateGameCommand command(final String id) {
        return UpdateGameCommand.with(
                id, "New Title", "new desc", "review", new BigDecimal("8.0"),
                List.of("pro"), List.of("con"), "https://cover",
                List.of("AVENTURA"), List.of("XBOX"));
    }

    @Test
    void givenValidCommand_whenExecute_thenUpdatesGame() {
        final var game = existingGame();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));
        when(gameGateway.existsByTitleAndIdNot(eq("New Title"), any(GameId.class))).thenReturn(false);
        when(gameGateway.save(any(Game.class))).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command(game.getId().getValue()));

        assertEquals("New Title", output.title());
        assertEquals(List.of("AVENTURA"), output.categories());
        verify(gameGateway).save(any(Game.class));
    }

    @Test
    void givenUnknownGame_whenExecute_thenThrowsNotFound() {
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(command(GameId.unique().getValue())));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenDeletedGame_whenExecute_thenThrowsNotFound() {
        final var game = existingGame();
        game.delete();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(command(game.getId().getValue())));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenDuplicateTitle_whenExecute_thenThrowsDuplicate() {
        final var game = existingGame();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));
        when(gameGateway.existsByTitleAndIdNot(eq("New Title"), any(GameId.class))).thenReturn(true);

        assertThrows(DuplicateEntityException.class,
                () -> useCase.execute(command(game.getId().getValue())));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenInvalidScore_whenExecute_thenThrowsNotification() {
        final var game = existingGame();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));
        final var command = UpdateGameCommand.with(
                game.getId().getValue(), "New Title", "desc", null, new BigDecimal("99.0"),
                List.of(), List.of(), null, List.of("RPG"), List.of("PC"));

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenGatewayFailureOnSave_whenExecute_thenPropagates() {
        final var game = existingGame();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));
        when(gameGateway.existsByTitleAndIdNot(anyString(), any(GameId.class))).thenReturn(false);
        when(gameGateway.save(any(Game.class))).thenThrow(new RuntimeException("db down"));

        assertThrows(RuntimeException.class, () -> useCase.execute(command(game.getId().getValue())));
    }
}
