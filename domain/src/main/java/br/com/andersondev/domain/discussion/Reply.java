package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.discussion.event.ReplyAddedEvent;
import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.time.Instant;

/**
 * Raiz do agregado Reply (domain-catalog 2.6).
 * likeCount e um contador desnormalizado mantido na mesma transacao da operacao de like.
 */
public class Reply extends AggregateRoot<ReplyId> {

    private final ThreadId threadId;
    private final UserId authorId;
    private final String content;
    private int likeCount;
    private final Instant createdAt;

    private Reply(
            final ReplyId id,
            final ThreadId threadId,
            final UserId authorId,
            final String content,
            final int likeCount,
            final Instant createdAt
    ) {
        super(id);
        this.threadId = threadId;
        this.authorId = authorId;
        this.content = content;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }

    /**
     * Cria uma nova resposta. likeCount inicia em 0.
     */
    public static Reply newReply(final ThreadId threadId, final UserId authorId, final String content) {
        final var reply = new Reply(
                ReplyId.unique(),
                threadId,
                authorId,
                content,
                0,
                Instant.now()
        );
        reply.registerEvent(ReplyAddedEvent.of(
                reply.getId().getValue(),
                threadId.getValue(),
                authorId.getValue()
        ));
        return reply;
    }

    /**
     * Reidrata uma resposta existente a partir da persistencia.
     */
    public static Reply with(
            final ReplyId id,
            final ThreadId threadId,
            final UserId authorId,
            final String content,
            final int likeCount,
            final Instant createdAt
    ) {
        return new Reply(id, threadId, authorId, content, likeCount, createdAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new ReplyValidator(this, handler).validate();
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public ThreadId getThreadId() {
        return this.threadId;
    }

    public UserId getAuthorId() {
        return this.authorId;
    }

    public String getContent() {
        return this.content;
    }

    public int getLikeCount() {
        return this.likeCount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }
}
