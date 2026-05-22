package br.com.andersondev.application.news.list;

import br.com.andersondev.application.UseCase;
import br.com.andersondev.application.news.NewsSummaryOutput;
import br.com.andersondev.domain.shared.Pagination;

public abstract class ListNewsUseCase extends UseCase<ListNewsCommand, Pagination<NewsSummaryOutput>> {
}
