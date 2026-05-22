package br.com.andersondev.application.moderation.list;

import br.com.andersondev.application.UseCase;
import br.com.andersondev.application.moderation.ReportOutput;
import br.com.andersondev.domain.shared.Pagination;

public abstract class ListReportsUseCase extends UseCase<ListReportsCommand, Pagination<ReportOutput>> {
}
