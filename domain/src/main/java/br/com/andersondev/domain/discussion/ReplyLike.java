package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.user.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * Representa o like de um usuario em uma resposta.
 * Identificado pelo par (replyId, userId) — sem id proprio.
 */
public class ReplyLike {

    private final ReplyId replyId;
    private final UserId userId;
    private final Instant likedAt;

    private ReplyLike(final ReplyId replyId, final UserId userId, final Instant likedAt) {
        this.replyId = Objects.requireNonNull(replyId, "'replyId' nao pode ser nulo");
        this.userId = Objects.requireNonNull(userId, "'userId' nao pode ser nulo");
        this.likedAt = Objects.requireNonNull(likedAt, "'likedAt' nao pode ser nulo");
    }

    public static ReplyLike of(final ReplyId replyId, final UserId userId) {
        return new ReplyLike(replyId, userId, Instant.now());
    }

    public static ReplyLike with(final ReplyId replyId, final UserId userId, final Instant likedAt) {
        return new ReplyLike(replyId, userId, likedAt);
    }

    public ReplyId getReplyId() {
        return this.replyId;
    }

    public UserId getUserId() {
        return this.userId;
    }

    public Instant getLikedAt() {
        return this.likedAt;
    }
}
