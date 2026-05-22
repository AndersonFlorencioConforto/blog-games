package br.com.andersondev.infrastructure.discussion.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Chave composta (reply_id, user_id) da entidade JPA {@link ReplyLikeJpaEntity}.
 */
@Embeddable
public class ReplyLikeId implements Serializable {

    @Column(name = "reply_id", nullable = false, length = 36)
    private String replyId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    public ReplyLikeId() {
    }

    public ReplyLikeId(final String replyId, final String userId) {
        this.replyId = replyId;
        this.userId = userId;
    }

    public String getReplyId() {
        return this.replyId;
    }

    public String getUserId() {
        return this.userId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReplyLikeId that = (ReplyLikeId) o;
        return Objects.equals(replyId, that.replyId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replyId, userId);
    }
}
