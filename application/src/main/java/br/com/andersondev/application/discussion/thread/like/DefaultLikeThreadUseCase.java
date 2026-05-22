package br.com.andersondev.application.discussion.thread.like;

import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.ThreadLike;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.discussion.port.ThreadLikeGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Curtir uma thread (POST /games/{gameId}/threads/{threadId}/likes).
 * Idempotente: se o usuario ja curtiu, nao faz nada.
 */
public final class DefaultLikeThreadUseCase extends LikeThreadUseCase {

    private final ThreadGateway threadGateway;
    private final ThreadLikeGateway threadLikeGateway;

    public DefaultLikeThreadUseCase(
            final ThreadGateway threadGateway,
            final ThreadLikeGateway threadLikeGateway
    ) {
        this.threadGateway = Objects.requireNonNull(threadGateway);
        this.threadLikeGateway = Objects.requireNonNull(threadLikeGateway);
    }

    @Override
    public void execute(final LikeThreadCommand command) {
        final var threadId = ThreadId.from(command.threadId());
        final var userId = UserId.from(command.userId());

        final var thread = this.threadGateway.findById(threadId)
                .orElseThrow(() -> EntityNotFoundException.with(Thread.class, threadId));

        if (this.threadLikeGateway.existsByThreadIdAndUserId(threadId, userId)) {
            return;
        }

        this.threadLikeGateway.save(ThreadLike.of(threadId, userId));
        thread.incrementLikeCount();
        this.threadGateway.save(thread);
    }
}
