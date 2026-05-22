package br.com.andersondev.application.discussion.reply.list;

import br.com.andersondev.application.discussion.reply.ReplyOutput;
import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.port.ReplyGateway;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.shared.Pagination;

import java.util.Objects;

/**
 * Lista respostas de uma thread paginadas.
 * Valida que a thread existe antes de buscar as respostas.
 */
public final class DefaultListRepliesUseCase extends ListRepliesUseCase {

    private final ReplyGateway replyGateway;
    private final ThreadGateway threadGateway;

    public DefaultListRepliesUseCase(final ReplyGateway replyGateway, final ThreadGateway threadGateway) {
        this.replyGateway = Objects.requireNonNull(replyGateway);
        this.threadGateway = Objects.requireNonNull(threadGateway);
    }

    @Override
    public Pagination<ReplyOutput> execute(final ListRepliesCommand command) {
        final var threadId = ThreadId.from(command.threadId());

        if (this.threadGateway.findById(threadId).isEmpty()) {
            throw EntityNotFoundException.with(Thread.class, threadId);
        }

        return this.replyGateway.findByThreadId(threadId, command.page(), command.size())
                .map(ReplyOutput::from);
    }
}
