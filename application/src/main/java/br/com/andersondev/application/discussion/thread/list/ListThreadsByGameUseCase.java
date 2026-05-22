package br.com.andersondev.application.discussion.thread.list;

import br.com.andersondev.application.UseCase;
import br.com.andersondev.domain.shared.Pagination;

public abstract class ListThreadsByGameUseCase
        extends UseCase<ListThreadsByGameCommand, Pagination<ThreadSummaryOutput>> {
}
