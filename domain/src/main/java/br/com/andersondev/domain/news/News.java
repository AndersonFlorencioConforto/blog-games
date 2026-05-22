package br.com.andersondev.domain.news;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.handler.ThrowsValidationHandler;

import java.time.Instant;

/**
 * Raiz do agregado News (domain-catalog 2.7).
 * Representa uma noticia do blog, podendo estar relacionada a um jogo.
 */
public class News extends AggregateRoot<NewsId> {

    private String title;
    private String summary;
    private String content;
    private String coverImageUrl;
    private final UserId authorId;
    private GameId relatedGameId;
    private final Instant publishedAt;
    private Instant updatedAt;

    private News(
            final NewsId id,
            final String title,
            final String summary,
            final String content,
            final String coverImageUrl,
            final UserId authorId,
            final GameId relatedGameId,
            final Instant publishedAt,
            final Instant updatedAt
    ) {
        super(id);
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
        this.authorId = authorId;
        this.relatedGameId = relatedGameId;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        selfValidate();
    }

    /**
     * Cria uma nova noticia. publishedAt e updatedAt sao definidos como Instant.now().
     */
    public static News newNews(
            final String title,
            final String summary,
            final String content,
            final String coverImageUrl,
            final UserId authorId,
            final GameId relatedGameId
    ) {
        final var now = Instant.now();
        return new News(
                NewsId.unique(),
                title,
                summary,
                content,
                coverImageUrl,
                authorId,
                relatedGameId,
                now,
                now
        );
    }

    /**
     * Reidrata uma noticia existente a partir da persistencia.
     */
    public static News with(
            final NewsId id,
            final String title,
            final String summary,
            final String content,
            final String coverImageUrl,
            final UserId authorId,
            final GameId relatedGameId,
            final Instant publishedAt,
            final Instant updatedAt
    ) {
        return new News(id, title, summary, content, coverImageUrl, authorId, relatedGameId, publishedAt, updatedAt);
    }

    /**
     * Atualiza os campos editaveis da noticia e revalida invariantes.
     */
    public News update(
            final String title,
            final String summary,
            final String content,
            final String coverImageUrl,
            final GameId relatedGameId
    ) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
        this.relatedGameId = relatedGameId;
        this.updatedAt = Instant.now();
        selfValidate();
        return this;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new NewsValidator(this, handler).validate();
    }

    private void selfValidate() {
        validate(new ThrowsValidationHandler());
    }

    public String getTitle() {
        return this.title;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getContent() {
        return this.content;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public UserId getAuthorId() {
        return this.authorId;
    }

    public GameId getRelatedGameId() {
        return this.relatedGameId;
    }

    public Instant getPublishedAt() {
        return this.publishedAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }
}
