package br.com.andersondev.application.news.get;

import br.com.andersondev.application.news.NewsOutput;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.NewsId;
import br.com.andersondev.domain.news.port.NewsGateway;

import java.util.Objects;

/**
 * Busca uma noticia por ID.
 */
public final class DefaultGetNewsByIdUseCase extends GetNewsByIdUseCase {

    private final NewsGateway newsGateway;

    public DefaultGetNewsByIdUseCase(final NewsGateway newsGateway) {
        this.newsGateway = Objects.requireNonNull(newsGateway);
    }

    @Override
    public NewsOutput execute(final GetNewsByIdCommand command) {
        final var newsId = NewsId.from(command.newsId());
        return this.newsGateway.findById(newsId)
                .map(NewsOutput::from)
                .orElseThrow(() -> EntityNotFoundException.with(News.class, newsId));
    }
}
