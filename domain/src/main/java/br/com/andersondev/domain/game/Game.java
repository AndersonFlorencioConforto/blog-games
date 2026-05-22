package br.com.andersondev.domain.game;

import br.com.andersondev.domain.game.event.GameCreatedEvent;
import br.com.andersondev.domain.game.event.GameDeletedEvent;
import br.com.andersondev.domain.game.event.GameUpdatedEvent;
import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Raiz do agregado Game (domain-catalog 2.2). Mantem a media de avaliacoes
 * desnormalizada ({@code averageUserRating}, {@code totalRatings}) atualizada
 * na mesma transacao do rating (transaction-boundaries / RateGameUseCase).
 * Suporta soft delete via {@code deletedAt} (P-013).
 */
public class Game extends AggregateRoot<GameId> {

    private static final int AVERAGE_SCALE = 2;

    private String title;
    private String description;
    private String editorialReview;
    private PlatformScore platformScore;
    private List<String> pros;
    private List<String> cons;
    private String coverImageUrl;
    private Set<Category> categories;
    private Set<Platform> platforms;
    private BigDecimal averageUserRating;
    private int totalRatings;
    private Instant deletedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private Game(
            final GameId id,
            final String title,
            final String description,
            final String editorialReview,
            final PlatformScore platformScore,
            final List<String> pros,
            final List<String> cons,
            final String coverImageUrl,
            final Set<Category> categories,
            final Set<Platform> platforms,
            final BigDecimal averageUserRating,
            final int totalRatings,
            final Instant deletedAt,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id);
        this.title = title;
        this.description = description;
        this.editorialReview = editorialReview;
        this.platformScore = platformScore;
        this.pros = copyList(pros);
        this.cons = copyList(cons);
        this.coverImageUrl = coverImageUrl;
        this.categories = copyCategories(categories);
        this.platforms = copyPlatforms(platforms);
        this.averageUserRating = averageUserRating;
        this.totalRatings = totalRatings;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Cria um novo jogo. Media e contador iniciam zerados.
     */
    public static Game newGame(
            final String title,
            final String description,
            final String editorialReview,
            final PlatformScore platformScore,
            final List<String> pros,
            final List<String> cons,
            final String coverImageUrl,
            final Set<Category> categories,
            final Set<Platform> platforms
    ) {
        final var now = Instant.now();
        final var game = new Game(
                GameId.unique(),
                title,
                description,
                editorialReview,
                platformScore,
                pros,
                cons,
                coverImageUrl,
                categories,
                platforms,
                BigDecimal.ZERO.setScale(AVERAGE_SCALE, RoundingMode.HALF_UP),
                0,
                null,
                now,
                now
        );
        game.registerEvent(GameCreatedEvent.of(game.getId().getValue(), game.getTitle()));
        return game;
    }

    /**
     * Reidrata um jogo existente a partir da persistencia.
     */
    public static Game with(
            final GameId id,
            final String title,
            final String description,
            final String editorialReview,
            final PlatformScore platformScore,
            final List<String> pros,
            final List<String> cons,
            final String coverImageUrl,
            final Set<Category> categories,
            final Set<Platform> platforms,
            final BigDecimal averageUserRating,
            final int totalRatings,
            final Instant deletedAt,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new Game(
                id, title, description, editorialReview, platformScore, pros, cons, coverImageUrl,
                categories, platforms, averageUserRating, totalRatings, deletedAt, createdAt, updatedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new GameValidator(this, handler).validate();
    }

    /**
     * Atualiza os atributos editaveis do jogo (UpdateGameUseCase).
     * Nao altera media/contador desnormalizados.
     */
    public void update(
            final String title,
            final String description,
            final String editorialReview,
            final PlatformScore platformScore,
            final List<String> pros,
            final List<String> cons,
            final String coverImageUrl,
            final Set<Category> categories,
            final Set<Platform> platforms
    ) {
        this.title = title;
        this.description = description;
        this.editorialReview = editorialReview;
        this.platformScore = platformScore;
        this.pros = copyList(pros);
        this.cons = copyList(cons);
        this.coverImageUrl = coverImageUrl;
        this.categories = copyCategories(categories);
        this.platforms = copyPlatforms(platforms);
        this.updatedAt = Instant.now();
        registerEvent(GameUpdatedEvent.of(this.getId().getValue(), this.title));
    }

    /**
     * Soft delete (P-013): marca {@code deletedAt} sem remover a linha,
     * preservando o historico de avaliacoes.
     */
    public void delete() {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
            this.updatedAt = this.deletedAt;
            registerEvent(GameDeletedEvent.of(this.getId().getValue()));
        }
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Aplica a media e o contador recalculados apos um rating
     * (transaction-boundaries / data-strategy secao 5). Normaliza a media para 2 casas.
     */
    public void applyRatingAggregate(final BigDecimal newAverage, final int newTotal) {
        if (newAverage == null) {
            this.averageUserRating = BigDecimal.ZERO.setScale(AVERAGE_SCALE, RoundingMode.HALF_UP);
        } else {
            this.averageUserRating = newAverage.setScale(AVERAGE_SCALE, RoundingMode.HALF_UP);
        }
        this.totalRatings = Math.max(newTotal, 0);
        this.updatedAt = Instant.now();
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getEditorialReview() {
        return this.editorialReview;
    }

    public PlatformScore getPlatformScore() {
        return this.platformScore;
    }

    public List<String> getPros() {
        return Collections.unmodifiableList(this.pros);
    }

    public List<String> getCons() {
        return Collections.unmodifiableList(this.cons);
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public Set<Category> getCategories() {
        return Collections.unmodifiableSet(this.categories);
    }

    public Set<Platform> getPlatforms() {
        return Collections.unmodifiableSet(this.platforms);
    }

    public BigDecimal getAverageUserRating() {
        return this.averageUserRating;
    }

    public int getTotalRatings() {
        return this.totalRatings;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    private static List<String> copyList(final List<String> values) {
        return values == null ? new ArrayList<>() : new ArrayList<>(values);
    }

    private static Set<Category> copyCategories(final Set<Category> values) {
        return values == null || values.isEmpty() ? EnumSet.noneOf(Category.class) : EnumSet.copyOf(values);
    }

    private static Set<Platform> copyPlatforms(final Set<Platform> values) {
        return values == null || values.isEmpty() ? EnumSet.noneOf(Platform.class) : EnumSet.copyOf(values);
    }
}
