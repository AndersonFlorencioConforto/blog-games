package br.com.andersondev.infrastructure.rating.persistence;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.rating.Rating;
import br.com.andersondev.domain.rating.RatingId;
import br.com.andersondev.domain.rating.Stars;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * Entidade JPA do agregado Rating. Unicidade (game_id, user_id) garante uma
 * avaliacao por par jogo+usuario. IDs como VARCHAR(36).
 */
@Entity(name = "Rating")
@Table(name = "ratings", uniqueConstraints = @UniqueConstraint(
        name = "uk_ratings_game_user", columnNames = {"game_id", "user_id"}))
public class RatingJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "game_id", nullable = false)
    private String gameId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "stars", nullable = false)
    private int stars;

    @Column(name = "rated_at", nullable = false)
    private Instant ratedAt;

    public RatingJpaEntity() {
    }

    public static RatingJpaEntity from(final Rating rating) {
        final var entity = new RatingJpaEntity();
        entity.id = rating.getId().getValue();
        entity.gameId = rating.getGameId().getValue();
        entity.userId = rating.getUserId().getValue();
        entity.stars = rating.getStars().getValue();
        entity.ratedAt = rating.getRatedAt();
        return entity;
    }

    public Rating toAggregate() {
        return Rating.with(
                RatingId.from(this.id),
                GameId.from(this.gameId),
                UserId.from(this.userId),
                Stars.of(this.stars),
                this.ratedAt
        );
    }
}
