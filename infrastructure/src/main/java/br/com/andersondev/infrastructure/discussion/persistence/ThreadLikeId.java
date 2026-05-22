package br.com.andersondev.infrastructure.discussion.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Chave composta (thread_id, user_id) da entidade JPA {@link ThreadLikeJpaEntity}.
 */
@Embeddable
public class ThreadLikeId implements Serializable {

    @Column(name = "thread_id", nullable = false, length = 36)
    private String threadId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    public ThreadLikeId() {
    }

    public ThreadLikeId(final String threadId, final String userId) {
        this.threadId = threadId;
        this.userId = userId;
    }

    public String getThreadId() {
        return this.threadId;
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
        final ThreadLikeId that = (ThreadLikeId) o;
        return Objects.equals(threadId, that.threadId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadId, userId);
    }
}
