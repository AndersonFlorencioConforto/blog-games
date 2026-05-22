package br.com.andersondev.domain.social;

import br.com.andersondev.domain.shared.Identifier;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Identificador composto do agregado Follow (domain-catalog 2.5): o par
 * (followerId, followedId) e suficiente como identidade; nao ha id proprio.
 */
public final class FollowId extends Identifier {

    private final UserId followerId;
    private final UserId followedId;

    private FollowId(final UserId followerId, final UserId followedId) {
        this.followerId = Objects.requireNonNull(followerId, "'followerId' nao pode ser nulo");
        this.followedId = Objects.requireNonNull(followedId, "'followedId' nao pode ser nulo");
    }

    public static FollowId of(final UserId followerId, final UserId followedId) {
        return new FollowId(followerId, followedId);
    }

    public UserId followerId() {
        return this.followerId;
    }

    public UserId followedId() {
        return this.followedId;
    }

    @Override
    public String getValue() {
        return this.followerId.getValue() + ":" + this.followedId.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FollowId followId = (FollowId) o;
        return Objects.equals(followerId, followId.followerId)
                && Objects.equals(followedId, followId.followedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followedId);
    }

    @Override
    public String toString() {
        return getValue();
    }
}
