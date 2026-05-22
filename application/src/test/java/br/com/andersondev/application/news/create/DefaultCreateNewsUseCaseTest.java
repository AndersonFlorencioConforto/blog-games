package br.com.andersondev.application.news.create;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.port.NewsGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCreateNewsUseCaseTest {

    @Mock
    private NewsGateway newsGateway;

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultCreateNewsUseCase useCase;

    private static final String VALID_TITLE = "Titulo valido da noticia";
    private static final String VALID_SUMMARY = "Resumo valido com mais de dez caracteres.";
    private static final String VALID_CONTENT = "Conteudo valido com mais de cinquenta caracteres para a noticia do blog de games.";
    private static final String AUTHOR_ID = UserId.unique().getValue();

    @Test
    void givenValidCommand_whenExecute_thenReturnOutput() {
        // Arrange
        final var command = CreateNewsCommand.with(
                VALID_TITLE, VALID_SUMMARY, VALID_CONTENT, null, AUTHOR_ID, null);

        when(newsGateway.save(any(News.class))).thenAnswer(returnsFirstArg());

        // Act
        final var output = useCase.execute(command);

        // Assert
        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals(VALID_TITLE, output.title());
        assertNull(output.relatedGameId());
        verify(newsGateway).save(any(News.class));
        verify(gameGateway, never()).findById(any());
    }

    @Test
    void givenValidCommandWithRelatedGame_whenExecute_thenReturnOutput() {
        // Arrange
        final var gameId = GameId.unique().getValue();
        final var command = CreateNewsCommand.with(
                VALID_TITLE, VALID_SUMMARY, VALID_CONTENT, "http://img.url", AUTHOR_ID, gameId);

        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(stubGame(false)));
        when(newsGateway.save(any(News.class))).thenAnswer(returnsFirstArg());

        // Act
        final var output = useCase.execute(command);

        // Assert
        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals(gameId, output.relatedGameId());
        verify(newsGateway).save(any(News.class));
    }

    @Test
    void givenNonExistentRelatedGame_whenExecute_thenThrowNotFoundException() {
        // Arrange
        final var gameId = GameId.unique().getValue();
        final var command = CreateNewsCommand.with(
                VALID_TITLE, VALID_SUMMARY, VALID_CONTENT, null, AUTHOR_ID, gameId);

        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(newsGateway, never()).save(any());
    }

    @Test
    void givenDeletedRelatedGame_whenExecute_thenThrowNotFoundException() {
        // Arrange
        final var gameId = GameId.unique().getValue();
        final var command = CreateNewsCommand.with(
                VALID_TITLE, VALID_SUMMARY, VALID_CONTENT, null, AUTHOR_ID, gameId);

        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(stubGame(true)));

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(newsGateway, never()).save(any());
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
