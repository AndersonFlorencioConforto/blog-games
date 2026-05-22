package br.com.andersondev.application.discussion.reply.unlike;

import br.com.andersondev.domain.discussion.Reply;
import br.com.andersondev.domain.discussion.ReplyId;
import br.com.andersondev.domain.discussion.port.ReplyGateway;
import br.com.andersondev.domain.discussion.port.ReplyLikeGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Remover o like de uma resposta (DELETE /threads/{threadId}/replies/{replyId}/likes).
 * Idempotente: se o usuario nao curtiu, nao faz nada.
 */
public final class DefaultUnlikeReplyUseCase extends UnlikeReplyUseCase {

    private final ReplyGateway replyGateway;
    private final ReplyLikeGateway replyLikeGateway;

    public DefaultUnlikeReplyUseCase(
            final ReplyGateway replyGateway,
            final ReplyLikeGateway replyLikeGateway
    ) {
        this.replyGateway = Objects.requireNonNull(replyGateway);
        this.replyLikeGateway = Objects.requireNonNull(replyLikeGateway);
    }

    @Override
    public void execute(final UnlikeReplyCommand command) {
        final var replyId = ReplyId.from(command.replyId());
        final var userId = UserId.from(command.userId());

        final var reply = this.replyGateway.findById(replyId)
                .orElseThrow(() -> EntityNotFoundException.with(Reply.class, replyId));

        if (!this.replyLikeGateway.existsByReplyIdAndUserId(replyId, userId)) {
            return;
        }

        this.replyLikeGateway.deleteByReplyIdAndUserId(replyId, userId);
        reply.decrementLikeCount();
        this.replyGateway.save(reply);
    }
}
