package br.com.andersondev.application.game.create;

import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.port.GameGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCreateGameUseCaseTest {

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultCreateGameUseCase useCase;

    private static CreateGameCommand validCommand() {
        return CreateGameCommand.with(
                "The Witcher 3",
                "RPG de mundo aberto",
                "Review",
                new BigDecimal("9.5"),
                List.of("Historia incrivel"),
                List.of("Lento"),
                "https://cdn/cover.jpg",
                List.of("RPG", "ACAO"),
                List.of("PC", "PLAYSTATION"));
    }

    @Test
    void givenValidCommand_whenExecute_thenCreatesGame() {
        when(gameGateway.existsByTitle("The Witcher 3")).thenReturn(false);
        when(gameGateway.save(any(Game.class))).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(validCommand());

        assertEquals("The Witcher 3", output.title());
        assertEquals(new BigDecimal("9.50"), output.platformScore());
        assertEquals(0, output.totalRatings());
        assertEquals(List.of("RPG", "ACAO"), output.categories());
        verify(gameGateway).save(any(Game.class));
    }

    @Test
    void givenExistingTitle_whenExecute_thenThrowsDuplicate() {
        when(gameGateway.existsByTitle("The Witcher 3")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> useCase.execute(validCommand()));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenBlankTitle_whenExecute_thenThrowsNotification() {
        final var command = CreateGameCommand.with(
                " ", "desc", null, new BigDecimal("5.0"),
                List.of(), List.of(), null, List.of("RPG"), List.of("PC"));

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenScoreOutOfRange_whenExecute_thenThrowsNotification() {
        final var command = CreateGameCommand.with(
                "Title", "desc", null, new BigDecimal("11.0"),
                List.of(), List.of(), null, List.of("RPG"), List.of("PC"));

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenNoCategory_whenExecute_thenThrowsNotification() {
        final var command = CreateGameCommand.with(
                "Title", "desc", null, new BigDecimal("5.0"),
                List.of(), List.of(), null, List.of(), List.of("PC"));

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenNoPlatform_whenExecute_thenThrowsNotification() {
        final var command = CreateGameCommand.with(
                "Title", "desc", null, new BigDecimal("5.0"),
                List.of(), List.of(), null, List.of("RPG"), List.of());

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenInvalidCategory_whenExecute_thenThrowsNotification() {
        final var command = CreateGameCommand.with(
                "Title", "desc", null, new BigDecimal("5.0"),
                List.of(), List.of(), null, List.of("FPS"), List.of("PC"));

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenInvalidPlatform_whenExecute_thenThrowsNotification() {
        final var command = CreateGameCommand.with(
                "Title", "desc", null, new BigDecimal("5.0"),
                List.of(), List.of(), null, List.of("RPG"), List.of("SEGA"));

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(gameGateway, never()).save(any());
    }

    @Test
    void givenGatewayFailureOnSave_whenExecute_thenPropagates() {
        when(gameGateway.existsByTitle(anyString())).thenReturn(false);
        when(gameGateway.save(any(Game.class))).thenThrow(new RuntimeException("db down"));

        assertThrows(RuntimeException.class, () -> useCase.execute(validCommand()));
    }
}
