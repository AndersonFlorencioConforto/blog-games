package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.discussion.event.ThreadCreatedEvent;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.handler.Notification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadTest {

    private static Thread validThread() {
        return Thread.newThread(
                GameId.unique(),
                UserId.unique(),
                "Titulo com mais de 5 chars",
                "Conteudo valido com mais de 10 caracteres para a thread."
        );
    }

    @Test
    void givenValidData_whenNewThread_thenCreatesWithZeroCountersAndEvent() {
        final var thread = validThread();

        assertNotNull(thread.getId());
        assertNotNull(thread.getGameId());
        assertNotNull(thread.getAuthorId());
        assertEquals(0, thread.getLikeCount());
        assertEquals(0, thread.getReplyCount());
        assertNotNull(thread.getCreatedAt());
        assertNotNull(thread.getUpdatedAt());
        assertEquals(1, thread.getDomainEvents().size());
        assertInstanceOf(ThreadCreatedEvent.class, thread.getDomainEvents().getFirst());
    }

    @Test
    void givenValidThread_whenValidate_thenNoErrors() {
        final var notification = Notification.create();

        validThread().validate(notification);

        assertFalse(notification.hasError());
    }

    @Test
    void givenTitleTooShort_whenValidate_thenHasError() {
        final var thread = Thread.newThread(
                GameId.unique(), UserId.unique(), "Hi", "Conteudo valido com mais de 10 caracteres.");
        final var notification = Notification.create();

        thread.validate(notification);

        assertTrue(notification.hasError());
        assertTrue(notification.getErrors().stream()
                .anyMatch(e -> e.message().contains("title")));
    }

    @Test
    void givenTitleTooLong_whenValidate_thenHasError() {
        final var longTitle = "A".repeat(201);
        final var thread = Thread.newThread(
                GameId.unique(), UserId.unique(), longTitle, "Conteudo valido com mais de 10 caracteres.");
        final var notification = Notification.create();

        thread.validate(notification);

        assertTrue(notification.hasError());
        assertTrue(notification.getErrors().stream()
                .anyMatch(e -> e.message().contains("title")));
    }

    @Test
    void givenContentTooShort_whenValidate_thenHasError() {
        final var thread = Thread.newThread(
                GameId.unique(), UserId.unique(), "Titulo valido", "Curto");
        final var notification = Notification.create();

        thread.validate(notification);

        assertTrue(notification.hasError());
        assertTrue(notification.getErrors().stream()
                .anyMatch(e -> e.message().contains("content")));
    }

    @Test
    void givenContentTooLong_whenValidate_thenHasError() {
        final var longContent = "A".repeat(5001);
        final var thread = Thread.newThread(
                GameId.unique(), UserId.unique(), "Titulo valido", longContent);
        final var notification = Notification.create();

        thread.validate(notification);

        assertTrue(notification.hasError());
        assertTrue(notification.getErrors().stream()
                .anyMatch(e -> e.message().contains("content")));
    }

    @Test
    void givenThread_whenIncrementLikeCount_thenCountIncreases() {
        final var thread = validThread();

        thread.incrementLikeCount();
        thread.incrementLikeCount();

        assertEquals(2, thread.getLikeCount());
    }

    @Test
    void givenThread_whenDecrementLikeCountBelowZero_thenStaysAtZero() {
        final var thread = validThread();

        thread.decrementLikeCount();

        assertEquals(0, thread.getLikeCount());
    }

    @Test
    void givenThread_whenIncrementReplyCount_thenCountIncreases() {
        final var thread = validThread();

        thread.incrementReplyCount();

        assertEquals(1, thread.getReplyCount());
    }
}
