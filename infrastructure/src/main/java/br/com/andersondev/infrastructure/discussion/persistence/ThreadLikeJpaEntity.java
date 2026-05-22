package br.com.andersondev.infrastructure.discussion.persistence;

import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.ThreadLike;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidade JPA de ThreadLike. PK composta via @EmbeddedId.
 */
@Entity(name = "ThreadLike")
@Table(name = "thread_likes")
public class ThreadLikeJpaEntity {

    @EmbeddedId
    private ThreadLikeId id;

    @Column(name = "liked_at", nullable = false)
    private Instant likedAt;

    public ThreadLikeJpaEntity() {
    }

    public static ThreadLikeJpaEntity from(final ThreadLike threadLike) {
        final var entity = new ThreadLikeJpaEntity();
        entity.id = new ThreadLikeId(threadLike.getThreadId().getValue(), threadLike.getUserId().getValue());
        entity.likedAt = threadLike.getLikedAt();
        return entity;
    }

    public ThreadLike toAggregate() {
        return ThreadLike.with(
                ThreadId.from(this.id.getThreadId()),
                UserId.from(this.id.getUserId()),
                this.likedAt
        );
    }

    public ThreadLikeId getId() {
        return this.id;
    }
}
