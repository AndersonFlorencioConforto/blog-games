package br.com.andersondev.infrastructure.discussion.persistence;

import br.com.andersondev.domain.discussion.ReplyId;
import br.com.andersondev.domain.discussion.ReplyLike;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidade JPA de ReplyLike. PK composta via @EmbeddedId.
 */
@Entity(name = "ReplyLike")
@Table(name = "reply_likes")
public class ReplyLikeJpaEntity {

    @EmbeddedId
    private ReplyLikeId id;

    @Column(name = "liked_at", nullable = false)
    private Instant likedAt;

    public ReplyLikeJpaEntity() {
    }

    public static ReplyLikeJpaEntity from(final ReplyLike replyLike) {
        final var entity = new ReplyLikeJpaEntity();
        entity.id = new ReplyLikeId(replyLike.getReplyId().getValue(), replyLike.getUserId().getValue());
        entity.likedAt = replyLike.getLikedAt();
        return entity;
    }

    public ReplyLike toAggregate() {
        return ReplyLike.with(
                ReplyId.from(this.id.getReplyId()),
                UserId.from(this.id.getUserId()),
                this.likedAt
        );
    }

    public ReplyLikeId getId() {
        return this.id;
    }
}
