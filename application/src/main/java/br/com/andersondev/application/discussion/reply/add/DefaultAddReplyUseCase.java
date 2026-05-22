package br.com.andersondev.application.discussion.reply.add;

import br.com.andersondev.application.discussion.reply.ReplyOutput;
import br.com.andersondev.domain.discussion.Reply;
import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.port.ReplyGateway;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Adiciona uma resposta a uma thread (POST /games/{gameId}/threads/{threadId}/replies).
 * Fluxo:
 *  1. Carrega a thread (404 se nao existir).
 *  2. Cria Reply e persiste.
 *  3. Incrementa replyCount na thread e salva.
 */
public final class DefaultAddReplyUseCase extends AddReplyUseCase {

    private final ReplyGateway replyGateway;
    private final ThreadGateway threadGateway;

    public DefaultAddReplyUseCase(final ReplyGateway replyGateway, final ThreadGateway threadGateway) {
        this.replyGateway = Objects.requireNonNull(replyGateway);
        this.threadGateway = Objects.requireNonNull(threadGateway);
    }

    @Override
    public ReplyOutput execute(final AddReplyCommand command) {
        final var threadId = ThreadId.from(command.threadId());
        final var authorId = UserId.from(command.authorId());

        final var thread = this.threadGateway.findById(threadId)
                .orElseThrow(() -> EntityNotFoundException.with(Thread.class, threadId));

        final var reply = Reply.newReply(threadId, authorId, command.content());
        final var savedReply = this.replyGateway.save(reply);

        thread.incrementReplyCount();
        this.threadGateway.save(thread);

        return ReplyOutput.from(savedReply);
    }
}
