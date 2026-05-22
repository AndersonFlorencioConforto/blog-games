package br.com.andersondev.application.news;

import br.com.andersondev.domain.news.News;

import java.time.Instant;

/**
 * Output resumido de noticia para listagens.
 */
public record NewsSummaryOutput(
        String id,
        String title,
        String summary,
        String coverImageUrl,
        String authorId,
        String relatedGameId,
        Instant publishedAt
) {

    public static NewsSummaryOutput from(final News news) {
        return new NewsSummaryOutput(
                news.getId().getValue(),
                news.getTitle(),
                news.getSummary(),
                news.getCoverImageUrl(),
                news.getAuthorId().getValue(),
                news.getRelatedGameId() != null ? news.getRelatedGameId().getValue() : null,
                news.getPublishedAt()
        );
    }
}
