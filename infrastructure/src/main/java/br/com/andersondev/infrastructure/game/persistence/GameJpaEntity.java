package br.com.andersondev.infrastructure.game.persistence;

import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.Platform;
import br.com.andersondev.domain.game.PlatformScore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Entidade JPA do agregado Game. pros/cons como colecoes ordenadas (P-014) e
 * categorias/plataformas como colecoes de enum em tabelas auxiliares (data-strategy).
 * IDs como VARCHAR(36) (mesmo padrao do auth).
 */
@Entity(name = "Game")
@Table(name = "games")
public class GameJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "title", nullable = false, unique = true, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 5000)
    private String description;

    @Column(name = "editorial_review", length = 10000)
    private String editorialReview;

    @Column(name = "platform_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal platformScore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_pros", joinColumns = @JoinColumn(name = "game_id"))
    @OrderColumn(name = "position")
    @Column(name = "value", length = 200)
    private List<String> pros = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_cons", joinColumns = @JoinColumn(name = "game_id"))
    @OrderColumn(name = "position")
    @Column(name = "value", length = 200)
    private List<String> cons = new ArrayList<>();

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_categories", joinColumns = @JoinColumn(name = "game_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    private Set<Category> categories = EnumSet.noneOf(Category.class);

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_platforms", joinColumns = @JoinColumn(name = "game_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 50)
    private Set<Platform> platforms = EnumSet.noneOf(Platform.class);

    @Column(name = "average_user_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageUserRating;

    @Column(name = "total_ratings", nullable = false)
    private int totalRatings;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public GameJpaEntity() {
    }

    public static GameJpaEntity from(final Game game) {
        final var entity = new GameJpaEntity();
        entity.id = game.getId().getValue();
        entity.title = game.getTitle();
        entity.description = game.getDescription();
        entity.editorialReview = game.getEditorialReview();
        entity.platformScore = game.getPlatformScore().getValue();
        entity.pros = new ArrayList<>(game.getPros());
        entity.cons = new ArrayList<>(game.getCons());
        entity.coverImageUrl = game.getCoverImageUrl();
        entity.categories = game.getCategories().isEmpty()
                ? EnumSet.noneOf(Category.class) : EnumSet.copyOf(game.getCategories());
        entity.platforms = game.getPlatforms().isEmpty()
                ? EnumSet.noneOf(Platform.class) : EnumSet.copyOf(game.getPlatforms());
        entity.averageUserRating = game.getAverageUserRating();
        entity.totalRatings = game.getTotalRatings();
        entity.deletedAt = game.getDeletedAt();
        entity.createdAt = game.getCreatedAt();
        entity.updatedAt = game.getUpdatedAt();
        return entity;
    }

    public Game toAggregate() {
        return Game.with(
                GameId.from(this.id),
                this.title,
                this.description,
                this.editorialReview,
                PlatformScore.of(this.platformScore),
                new ArrayList<>(this.pros),
                new ArrayList<>(this.cons),
                this.coverImageUrl,
                this.categories.isEmpty() ? EnumSet.noneOf(Category.class) : EnumSet.copyOf(this.categories),
                this.platforms.isEmpty() ? EnumSet.noneOf(Platform.class) : EnumSet.copyOf(this.platforms),
                this.averageUserRating,
                this.totalRatings,
                this.deletedAt,
                this.createdAt,
                this.updatedAt
        );
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }
}
