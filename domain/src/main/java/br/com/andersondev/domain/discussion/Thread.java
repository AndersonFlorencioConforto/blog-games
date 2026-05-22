package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.discussion.event.ThreadCreatedEvent;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.time.Instant;

/**
 * Raiz do agregado Thread (domain-catalog 2.6).
 * likeCount e replyCount sao contadores desnormalizados mantidos na mesma transacao
 * das operacoes de like e reply.
 */
public class Thread extends AggregateRoot<ThreadId> {

    private final GameId gameId;
    private final UserId authorId;
    private final String title;
    private final String content;
    private int likeCount;
    private int replyCount;
    private final Instant createdAt;
    private Instant updatedAt;

    private Thread(
            final ThreadId id,
            final GameId gameId,
            final UserId authorId,
            final String title,
            final String content,
            final int likeCount,
            final int replyCount,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id);
        this.gameId = gameId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Cria uma nova thread. likeCount e replyCount iniciam em 0.
     */
    public static Thread newThread(
            final GameId gameId,
            final UserId authorId,
            final String title,
            final String content
    ) {
        final var now = Instant.now();
        final var thread = new Thread(
                ThreadId.unique(),
                gameId,
                authorId,
                title,
                content,
                0,
                0,
                now,
                now
        );
        thread.registerEvent(ThreadCreatedEvent.of(
                thread.getId().getValue(),
                gameId.getValue(),
                authorId.getValue()
        ));
        return thread;
    }

    /**
     * Reidrata uma thread existente a partir da persistencia.
     */
    public static Thread with(
            final ThreadId id,
            final GameId gameId,
            final UserId authorId,
            final String title,
            final String content,
            final int likeCount,
            final int replyCount,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new Thread(id, gameId, authorId, title, content, likeCount, replyCount, createdAt, updatedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new ThreadValidator(this, handler).validate();
    }

    public void incrementLikeCount() {
        this.likeCount++;
        this.updatedAt = Instant.now();
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
        this.updatedAt = Instant.now();
    }

    public void incrementReplyCount() {
        this.replyCount++;
        this.updatedAt = Instant.now();
    }

    public GameId getGameId() {
        return this.gameId;
    }

    public UserId getAuthorId() {
        return this.authorId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public int getLikeCount() {
        return this.likeCount;
    }

    public int getReplyCount() {
        return this.replyCount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }
}
