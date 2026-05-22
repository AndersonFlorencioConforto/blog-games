package br.com.andersondev.application.discussion.thread.create;

import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCreateThreadUseCaseTest {

    @Mock
    private ThreadGateway threadGateway;

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultCreateThreadUseCase useCase;

    @Test
    void givenValidCommandAndGameExists_whenExecute_thenCreatesThread() {
        // Arrange
        final var gameId = GameId.unique().getValue();
        final var authorId = "user-1";
        final var command = CreateThreadCommand.with(gameId, authorId, "Titulo valido aqui", "Conteudo valido com mais de 10 caracteres.");

        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(stubGame(false)));
        when(threadGateway.save(any(Thread.class))).thenAnswer(returnsFirstArg());

        // Act
        final var output = useCase.execute(command);

        // Assert
        assertNotNull(output);
        assertNotNull(output.id());
        verify(threadGateway).save(any(Thread.class));
    }

    @Test
    void givenGameNotFound_whenExecute_thenThrowsEntityNotFoundException() {
        // Arrange
        final var gameId = GameId.unique().getValue();
        final var command = CreateThreadCommand.with(gameId, "user-1", "Titulo valido aqui", "Conteudo valido com mais de 10 caracteres.");

        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(threadGateway, never()).save(any());
    }

    @Test
    void givenDeletedGame_whenExecute_thenThrowsEntityNotFoundException() {
        // Arrange
        final var gameId = GameId.unique().getValue();
        final var command = CreateThreadCommand.with(gameId, "user-1", "Titulo valido aqui", "Conteudo valido com mais de 10 caracteres.");

        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(stubGame(true)));

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(threadGateway, never()).save(any());
    }

    @Test
    void givenGatewayFailure_whenExecute_thenPropagates() {
        // Arrange
        final var gameId = GameId.unique().getValue();
        final var command = CreateThreadCommand.with(gameId, "user-1", "Titulo valido aqui", "Conteudo valido com mais de 10 caracteres.");

        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(stubGame(false)));
        when(threadGateway.save(any(Thread.class))).thenThrow(new RuntimeException("db down"));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> useCase.execute(command));
    }

    private static Game stubGame(final boolean deleted) {
        final var game = Game.newGame(
                "Game Title",
                "Description long enough",
                null,
                br.com.andersondev.domain.game.PlatformScore.of(new java.math.BigDecimal("8.0")),
                java.util.List.of(),
                java.util.List.of(),
                null,
                java.util.EnumSet.of(br.com.andersondev.domain.game.Category.ACAO),
                java.util.EnumSet.of(br.com.andersondev.domain.game.Platform.PC)
        );
        if (deleted) {
            game.delete();
        }
        return game;
    }
}
