package br.com.andersondev.infrastructure.news.persistence;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.NewsId;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidade JPA do agregado News. IDs como VARCHAR(36).
 */
@Entity(name = "News")
@Table(name = "news")
public class NewsJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "summary", nullable = false, length = 500)
    private String summary;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image_url", length = 2048)
    private String coverImageUrl;

    @Column(name = "author_id", nullable = false, length = 36)
    private String authorId;

    @Column(name = "related_game_id", nullable = true, length = 36)
    private String relatedGameId;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public NewsJpaEntity() {
    }

    public static NewsJpaEntity from(final News news) {
        final var entity = new NewsJpaEntity();
        entity.id = news.getId().getValue();
        entity.title = news.getTitle();
        entity.summary = news.getSummary();
        entity.content = news.getContent();
        entity.coverImageUrl = news.getCoverImageUrl();
        entity.authorId = news.getAuthorId().getValue();
        entity.relatedGameId = news.getRelatedGameId() != null ? news.getRelatedGameId().getValue() : null;
        entity.publishedAt = news.getPublishedAt();
        entity.updatedAt = news.getUpdatedAt();
        return entity;
    }

    public News toAggregate() {
        return News.with(
                NewsId.from(this.id),
                this.title,
                this.summary,
                this.content,
                this.coverImageUrl,
                UserId.from(this.authorId),
                this.relatedGameId != null ? GameId.from(this.relatedGameId) : null,
                this.publishedAt,
                this.updatedAt
        );
    }

    public String getId() {
        return this.id;
    }

    public String getRelatedGameId() {
        return this.relatedGameId;
    }
}
