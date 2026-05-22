package br.com.andersondev.application.discussion.thread.get;

import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;

import java.util.Objects;

/**
 * Busca uma thread pelo ID (GET /games/{gameId}/threads/{threadId}).
 */
public final class DefaultGetThreadByIdUseCase extends GetThreadByIdUseCase {

    private final ThreadGateway threadGateway;

    public DefaultGetThreadByIdUseCase(final ThreadGateway threadGateway) {
        this.threadGateway = Objects.requireNonNull(threadGateway);
    }

    @Override
    public ThreadDetailOutput execute(final GetThreadByIdCommand command) {
        final var threadId = ThreadId.from(command.threadId());
        return this.threadGateway.findById(threadId)
                .map(ThreadDetailOutput::from)
                .orElseThrow(() -> EntityNotFoundException.with(Thread.class, threadId));
    }
}
