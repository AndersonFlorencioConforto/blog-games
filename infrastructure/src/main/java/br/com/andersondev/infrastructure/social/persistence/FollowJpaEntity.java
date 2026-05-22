package br.com.andersondev.infrastructure.social.persistence;

import br.com.andersondev.domain.social.Follow;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidade JPA do agregado Follow. Chave composta (follower_id, followed_id),
 * sem id proprio (domain-catalog 2.5). IDs como VARCHAR(36).
 */
@Entity(name = "Follow")
@Table(name = "follows")
@IdClass(FollowId.class)
public class FollowJpaEntity {

    @Id
    @Column(name = "follower_id", nullable = false, length = 36)
    private String followerId;

    @Id
    @Column(name = "followed_id", nullable = false, length = 36)
    private String followedId;

    @Column(name = "followed_at", nullable = false)
    private Instant followedAt;

    public FollowJpaEntity() {
    }

    public static FollowJpaEntity from(final Follow follow) {
        final var entity = new FollowJpaEntity();
        entity.followerId = follow.getFollowerId().getValue();
        entity.followedId = follow.getFollowedId().getValue();
        entity.followedAt = follow.getFollowedAt();
        return entity;
    }

    public Follow toAggregate() {
        return Follow.with(
                UserId.from(this.followerId),
                UserId.from(this.followedId),
                this.followedAt
        );
    }

    public String getFollowerId() {
        return this.followerId;
    }

    public String getFollowedId() {
        return this.followedId;
    }
}
