package br.com.andersondev.domain.discussion.event;

import br.com.andersondev.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Emitido apos adicao de uma resposta em uma thread.
 */
public record ReplyAddedEvent(String replyId, String threadId, String authorId, Instant occurredOn)
        implements DomainEvent {

    public static ReplyAddedEvent of(final String replyId, final String threadId, final String authorId) {
        return new ReplyAddedEvent(replyId, threadId, authorId, Instant.now());
    }
}
