package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.discussion.event.ReplyAddedEvent;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.handler.Notification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplyTest {

    private static Reply validReply() {
        return Reply.newReply(ThreadId.unique(), UserId.unique(), "Resposta valida com conteudo.");
    }

    @Test
    void givenValidData_whenNewReply_thenCreatesWithZeroLikeCountAndEvent() {
        final var reply = validReply();

        assertNotNull(reply.getId());
        assertNotNull(reply.getThreadId());
        assertNotNull(reply.getAuthorId());
        assertNotNull(reply.getContent());
        assertEquals(0, reply.getLikeCount());
        assertNotNull(reply.getCreatedAt());
        assertEquals(1, reply.getDomainEvents().size());
        assertInstanceOf(ReplyAddedEvent.class, reply.getDomainEvents().getFirst());
    }

    @Test
    void givenValidReply_whenValidate_thenNoErrors() {
        final var notification = Notification.create();

        validReply().validate(notification);

        assertFalse(notification.hasError());
    }

    @Test
    void givenBlankContent_whenValidate_thenHasError() {
        final var reply = Reply.newReply(ThreadId.unique(), UserId.unique(), "   ");
        final var notification = Notification.create();

        reply.validate(notification);

        assertTrue(notification.hasError());
        assertTrue(notification.getErrors().stream()
                .anyMatch(e -> e.message().contains("content")));
    }

    @Test
    void givenContentTooLong_whenValidate_thenHasError() {
        final var longContent = "A".repeat(2001);
        final var reply = Reply.newReply(ThreadId.unique(), UserId.unique(), longContent);
        final var notification = Notification.create();

        reply.validate(notification);

        assertTrue(notification.hasError());
        assertTrue(notification.getErrors().stream()
                .anyMatch(e -> e.message().contains("content")));
    }

    @Test
    void givenReply_whenIncrementThenDecrement_thenCountReturnsToZero() {
        final var reply = validReply();

        reply.incrementLikeCount();
        assertEquals(1, reply.getLikeCount());

        reply.decrementLikeCount();
        assertEquals(0, reply.getLikeCount());
    }

    @Test
    void givenReplyWithZeroLikes_whenDecrementLikeCount_thenStaysAtZero() {
        final var reply = validReply();

        reply.decrementLikeCount();

        assertEquals(0, reply.getLikeCount());
    }
}
