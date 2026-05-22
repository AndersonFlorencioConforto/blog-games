package br.com.andersondev.application.news.delete;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.NewsId;
import br.com.andersondev.domain.news.port.NewsGateway;

import java.util.Objects;

/**
 * Exclui uma noticia existente.
 * Fluxo:
 *  1. Verifica que a noticia existe (404 caso contrario).
 *  2. Chama newsGateway.deleteById.
 */
public final class DefaultDeleteNewsUseCase extends DeleteNewsUseCase {

    private final NewsGateway newsGateway;

    public DefaultDeleteNewsUseCase(final NewsGateway newsGateway) {
        this.newsGateway = Objects.requireNonNull(newsGateway);
    }

    @Override
    public Void execute(final DeleteNewsCommand command) {
        final var newsId = NewsId.from(command.newsId());
        this.newsGateway.findById(newsId)
                .orElseThrow(() -> EntityNotFoundException.with(News.class, newsId));
        this.newsGateway.deleteById(newsId);
        return null;
    }
}
