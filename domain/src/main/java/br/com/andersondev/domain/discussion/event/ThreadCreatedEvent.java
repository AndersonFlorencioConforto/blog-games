package br.com.andersondev.domain.discussion.event;

import br.com.andersondev.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Emitido apos criacao de uma thread de discussao.
 */
public record ThreadCreatedEvent(String threadId, String gameId, String authorId, Instant occurredOn)
        implements DomainEvent {

    public static ThreadCreatedEvent of(final String threadId, final String gameId, final String authorId) {
        return new ThreadCreatedEvent(threadId, gameId, authorId, Instant.now());
    }
}
