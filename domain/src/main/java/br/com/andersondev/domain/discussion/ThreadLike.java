package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.user.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * Representa o like de um usuario em uma thread.
 * Identificado pelo par (threadId, userId) — sem id proprio.
 */
public class ThreadLike {

    private final ThreadId threadId;
    private final UserId userId;
    private final Instant likedAt;

    private ThreadLike(final ThreadId threadId, final UserId userId, final Instant likedAt) {
        this.threadId = Objects.requireNonNull(threadId, "'threadId' nao pode ser nulo");
        this.userId = Objects.requireNonNull(userId, "'userId' nao pode ser nulo");
        this.likedAt = Objects.requireNonNull(likedAt, "'likedAt' nao pode ser nulo");
    }

    public static ThreadLike of(final ThreadId threadId, final UserId userId) {
        return new ThreadLike(threadId, userId, Instant.now());
    }

    public static ThreadLike with(final ThreadId threadId, final UserId userId, final Instant likedAt) {
        return new ThreadLike(threadId, userId, likedAt);
    }

    public ThreadId getThreadId() {
        return this.threadId;
    }

    public UserId getUserId() {
        return this.userId;
    }

    public Instant getLikedAt() {
        return this.likedAt;
    }
}
