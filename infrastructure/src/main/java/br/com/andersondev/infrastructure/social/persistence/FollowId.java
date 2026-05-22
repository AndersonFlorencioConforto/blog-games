package br.com.andersondev.infrastructure.social.persistence;

import java.io.Serializable;
import java.util.Objects;

/**
 * Chave composta (follower_id, followed_id) da entidade JPA {@link FollowJpaEntity}.
 */
public class FollowId implements Serializable {

    private String followerId;
    private String followedId;

    public FollowId() {
    }

    public FollowId(final String followerId, final String followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public String getFollowedId() {
        return followedId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FollowId that = (FollowId) o;
        return Objects.equals(followerId, that.followerId) && Objects.equals(followedId, that.followedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followedId);
    }
}
