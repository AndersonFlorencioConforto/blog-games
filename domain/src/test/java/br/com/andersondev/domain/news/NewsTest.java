package br.com.andersondev.domain.news;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.handler.Notification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsTest {

    private static final String VALID_TITLE = "Titulo valido da noticia";
    private static final String VALID_SUMMARY = "Resumo valido com mais de dez caracteres para a noticia.";
    private static final String VALID_CONTENT = "Conteudo valido com mais de cinquenta caracteres para a noticia do blog de games.";
    private static final UserId AUTHOR_ID = UserId.unique();

    @Test
    void givenValidParams_whenNewNews_thenCreateNews() {
        // Arrange + Act
        final var news = News.newNews(VALID_TITLE, VALID_SUMMARY, VALID_CONTENT, null, AUTHOR_ID, null);

        // Assert
        assertNotNull(news.getId());
        assertEquals(VALID_TITLE, news.getTitle());
        assertEquals(VALID_SUMMARY, news.getSummary());
        assertEquals(VALID_CONTENT, news.getContent());
        assertNull(news.getCoverImageUrl());
        assertEquals(AUTHOR_ID, news.getAuthorId());
        assertNull(news.getRelatedGameId());
        assertNotNull(news.getPublishedAt());
        assertNotNull(news.getUpdatedAt());
    }

    @Test
    void givenValidParamsWithRelatedGame_whenNewNews_thenCreateNewsWithGameId() {
        // Arrange
        final var gameId = GameId.unique();

        // Act
        final var news = News.newNews(VALID_TITLE, VALID_SUMMARY, VALID_CONTENT, "http://img.url/cover.png", AUTHOR_ID, gameId);

        // Assert
        assertNotNull(news.getId());
        assertEquals(gameId, news.getRelatedGameId());
        assertEquals("http://img.url/cover.png", news.getCoverImageUrl());
    }

    @Test
    void givenShortTitle_whenNewNews_thenThrowValidationError() {
        // Arrange + Act + Assert
        final var ex = assertThrows(DomainException.class, () ->
                News.newNews("Hi", VALID_SUMMARY, VALID_CONTENT, null, AUTHOR_ID, null));

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.message().contains("title")));
    }

    @Test
    void givenLongTitle_whenNewNews_thenThrowValidationError() {
        // Arrange
        final var longTitle = "A".repeat(301);

        // Act + Assert
        final var ex = assertThrows(DomainException.class, () ->
                News.newNews(longTitle, VALID_SUMMARY, VALID_CONTENT, null, AUTHOR_ID, null));

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.message().contains("title")));
    }

    @Test
    void givenLongSummary_whenNewNews_thenThrowValidationError() {
        // Arrange
        final var longSummary = "A".repeat(501);

        // Act + Assert
        final var ex = assertThrows(DomainException.class, () ->
                News.newNews(VALID_TITLE, longSummary, VALID_CONTENT, null, AUTHOR_ID, null));

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.message().contains("summary")));
    }

    @Test
    void givenShortContent_whenNewNews_thenThrowValidationError() {
        // Arrange
        final var shortContent = "Curto demais";

        // Act + Assert
        final var ex = assertThrows(DomainException.class, () ->
                News.newNews(VALID_TITLE, VALID_SUMMARY, shortContent, null, AUTHOR_ID, null));

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.message().contains("content")));
    }

    @Test
    void givenValidUpdate_whenUpdate_thenUpdatedFieldsAndUpdatedAt() throws InterruptedException {
        // Arrange
        final var news = News.newNews(VALID_TITLE, VALID_SUMMARY, VALID_CONTENT, null, AUTHOR_ID, null);
        final var originalUpdatedAt = news.getUpdatedAt();
        final var newGameId = GameId.unique();

        // Small delay to ensure updatedAt changes
        Thread.sleep(2);

        // Act
        news.update(
                "Titulo atualizado da noticia",
                "Resumo atualizado com mais de dez caracteres.",
                "Conteudo atualizado com mais de cinquenta caracteres para a noticia do blog de games.",
                "http://cover.url/img.png",
                newGameId
        );

        // Assert
        assertEquals("Titulo atualizado da noticia", news.getTitle());
        assertEquals("Resumo atualizado com mais de dez caracteres.", news.getSummary());
        assertEquals(newGameId, news.getRelatedGameId());
        assertFalse(news.getUpdatedAt().equals(originalUpdatedAt));
    }

    @Test
    void givenValidNews_whenValidate_thenNoErrors() {
        // Arrange
        final var news = News.newNews(VALID_TITLE, VALID_SUMMARY, VALID_CONTENT, null, AUTHOR_ID, null);
        final var notification = Notification.create();

        // Act
        news.validate(notification);

        // Assert
        assertFalse(notification.hasError());
    }
}
