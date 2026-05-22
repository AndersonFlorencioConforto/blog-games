package br.com.andersondev.application.news;

import br.com.andersondev.domain.news.News;

import java.time.Instant;

/**
 * Output completo de uma noticia (criacao, consulta por id, update).
 */
public record NewsOutput(
        String id,
        String title,
        String summary,
        String content,
        String coverImageUrl,
        String authorId,
        String relatedGameId,
        Instant publishedAt,
        Instant updatedAt
) {

    public static NewsOutput from(final News news) {
        return new NewsOutput(
                news.getId().getValue(),
                news.getTitle(),
                news.getSummary(),
                news.getContent(),
                news.getCoverImageUrl(),
                news.getAuthorId().getValue(),
                news.getRelatedGameId() != null ? news.getRelatedGameId().getValue() : null,
                news.getPublishedAt(),
                news.getUpdatedAt()
        );
    }
}
