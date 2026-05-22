package br.com.andersondev.application.news.delete;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.NewsId;
import br.com.andersondev.domain.news.port.NewsGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultDeleteNewsUseCaseTest {

    @Mock
    private NewsGateway newsGateway;

    @InjectMocks
    private DefaultDeleteNewsUseCase useCase;

    @Test
    void givenExistingNews_whenExecute_thenDeleteCalled() {
        // Arrange
        final var newsId = NewsId.unique().getValue();
        final var command = DeleteNewsCommand.with(newsId);
        final var news = stubNews(newsId);

        when(newsGateway.findById(any(NewsId.class))).thenReturn(Optional.of(news));

        // Act
        useCase.execute(command);

        // Assert
        verify(newsGateway).findById(any(NewsId.class));
        verify(newsGateway).deleteById(any(NewsId.class));
    }

    @Test
    void givenNonExistentNews_whenExecute_thenThrowNotFoundException() {
        // Arrange
        final var newsId = NewsId.unique().getValue();
        final var command = DeleteNewsCommand.with(newsId);

        when(newsGateway.findById(any(NewsId.class))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(newsGateway, never()).deleteById(any());
    }

    private static News stubNews(final String id) {
        return News.with(
                NewsId.from(id),
                "Titulo valido da noticia",
                "Resumo valido com mais de dez caracteres para a noticia.",
                "Conteudo valido com mais de cinquenta caracteres para a noticia do blog de games.",
                null,
                UserId.unique(),
                null,
                java.time.Instant.now(),
                java.time.Instant.now()
        );
    }
}
