package br.com.andersondev.infrastructure.discussion.persistence;

import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidade JPA do agregado Thread. IDs como VARCHAR(36).
 */
@Entity(name = "Thread")
@Table(name = "threads")
public class ThreadJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "game_id", nullable = false, length = 36)
    private String gameId;

    @Column(name = "author_id", nullable = false, length = 36)
    private String authorId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "reply_count", nullable = false)
    private int replyCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ThreadJpaEntity() {
    }

    public static ThreadJpaEntity from(final Thread thread) {
        final var entity = new ThreadJpaEntity();
        entity.id = thread.getId().getValue();
        entity.gameId = thread.getGameId().getValue();
        entity.authorId = thread.getAuthorId().getValue();
        entity.title = thread.getTitle();
        entity.content = thread.getContent();
        entity.likeCount = thread.getLikeCount();
        entity.replyCount = thread.getReplyCount();
        entity.createdAt = thread.getCreatedAt();
        entity.updatedAt = thread.getUpdatedAt();
        return entity;
    }

    public Thread toAggregate() {
        return Thread.with(
                ThreadId.from(this.id),
                GameId.from(this.gameId),
                UserId.from(this.authorId),
                this.title,
                this.content,
                this.likeCount,
                this.replyCount,
                this.createdAt,
                this.updatedAt
        );
    }

    public String getId() {
        return this.id;
    }

    public String getGameId() {
        return this.gameId;
    }
}
