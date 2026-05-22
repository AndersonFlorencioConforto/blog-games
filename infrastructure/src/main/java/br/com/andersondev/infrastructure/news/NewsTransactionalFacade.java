package br.com.andersondev.infrastructure.news;

import br.com.andersondev.application.news.NewsOutput;
import br.com.andersondev.application.news.NewsSummaryOutput;
import br.com.andersondev.application.news.create.CreateNewsCommand;
import br.com.andersondev.application.news.create.CreateNewsUseCase;
import br.com.andersondev.application.news.delete.DeleteNewsCommand;
import br.com.andersondev.application.news.delete.DeleteNewsUseCase;
import br.com.andersondev.application.news.get.GetNewsByIdCommand;
import br.com.andersondev.application.news.get.GetNewsByIdUseCase;
import br.com.andersondev.application.news.list.ListNewsCommand;
import br.com.andersondev.application.news.list.ListNewsUseCase;
import br.com.andersondev.application.news.update.UpdateNewsCommand;
import br.com.andersondev.application.news.update.UpdateNewsUseCase;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fronteiras transacionais dos casos de uso de Noticias.
 * Os casos de uso sao Java puro (application layer), sem Spring.
 */
@Service
public class NewsTransactionalFacade {

    private final CreateNewsUseCase createNewsUseCase;
    private final UpdateNewsUseCase updateNewsUseCase;
    private final DeleteNewsUseCase deleteNewsUseCase;
    private final GetNewsByIdUseCase getNewsByIdUseCase;
    private final ListNewsUseCase listNewsUseCase;

    public NewsTransactionalFacade(
            final CreateNewsUseCase createNewsUseCase,
            final UpdateNewsUseCase updateNewsUseCase,
            final DeleteNewsUseCase deleteNewsUseCase,
            final GetNewsByIdUseCase getNewsByIdUseCase,
            final ListNewsUseCase listNewsUseCase
    ) {
        this.createNewsUseCase = createNewsUseCase;
        this.updateNewsUseCase = updateNewsUseCase;
        this.deleteNewsUseCase = deleteNewsUseCase;
        this.getNewsByIdUseCase = getNewsByIdUseCase;
        this.listNewsUseCase = listNewsUseCase;
    }

    @Transactional
    public NewsOutput createNews(final CreateNewsCommand command) {
        return this.createNewsUseCase.execute(command);
    }

    @Transactional
    public NewsOutput updateNews(final UpdateNewsCommand command) {
        return this.updateNewsUseCase.execute(command);
    }

    @Transactional
    public void deleteNews(final DeleteNewsCommand command) {
        this.deleteNewsUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public NewsOutput getNewsById(final GetNewsByIdCommand command) {
        return this.getNewsByIdUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public Pagination<NewsSummaryOutput> listNews(final ListNewsCommand command) {
        return this.listNewsUseCase.execute(command);
    }
}
