package br.com.andersondev.infrastructure.discussion.persistence;

import br.com.andersondev.domain.discussion.Reply;
import br.com.andersondev.domain.discussion.ReplyId;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidade JPA do agregado Reply. IDs como VARCHAR(36).
 */
@Entity(name = "Reply")
@Table(name = "replies")
public class ReplyJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "thread_id", nullable = false, length = 36)
    private String threadId;

    @Column(name = "author_id", nullable = false, length = 36)
    private String authorId;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ReplyJpaEntity() {
    }

    public static ReplyJpaEntity from(final Reply reply) {
        final var entity = new ReplyJpaEntity();
        entity.id = reply.getId().getValue();
        entity.threadId = reply.getThreadId().getValue();
        entity.authorId = reply.getAuthorId().getValue();
        entity.content = reply.getContent();
        entity.likeCount = reply.getLikeCount();
        entity.createdAt = reply.getCreatedAt();
        return entity;
    }

    public Reply toAggregate() {
        return Reply.with(
                ReplyId.from(this.id),
                ThreadId.from(this.threadId),
                UserId.from(this.authorId),
                this.content,
                this.likeCount,
                this.createdAt
        );
    }

    public String getId() {
        return this.id;
    }

    public String getThreadId() {
        return this.threadId;
    }
}
