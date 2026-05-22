package br.com.andersondev.application.discussion.reply.like;

import br.com.andersondev.domain.discussion.Reply;
import br.com.andersondev.domain.discussion.ReplyId;
import br.com.andersondev.domain.discussion.ReplyLike;
import br.com.andersondev.domain.discussion.port.ReplyGateway;
import br.com.andersondev.domain.discussion.port.ReplyLikeGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Curtir uma resposta (POST /threads/{threadId}/replies/{replyId}/likes).
 * Idempotente: se o usuario ja curtiu, nao faz nada.
 */
public final class DefaultLikeReplyUseCase extends LikeReplyUseCase {

    private final ReplyGateway replyGateway;
    private final ReplyLikeGateway replyLikeGateway;

    public DefaultLikeReplyUseCase(
            final ReplyGateway replyGateway,
            final ReplyLikeGateway replyLikeGateway
    ) {
        this.replyGateway = Objects.requireNonNull(replyGateway);
        this.replyLikeGateway = Objects.requireNonNull(replyLikeGateway);
    }

    @Override
    public void execute(final LikeReplyCommand command) {
        final var replyId = ReplyId.from(command.replyId());
        final var userId = UserId.from(command.userId());

        final var reply = this.replyGateway.findById(replyId)
                .orElseThrow(() -> EntityNotFoundException.with(Reply.class, replyId));

        if (this.replyLikeGateway.existsByReplyIdAndUserId(replyId, userId)) {
            return;
        }

        this.replyLikeGateway.save(ReplyLike.of(replyId, userId));
        reply.incrementLikeCount();
        this.replyGateway.save(reply);
    }
}
