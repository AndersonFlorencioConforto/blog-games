package br.com.andersondev.infrastructure.moderation;

import br.com.andersondev.application.moderation.ReportOutput;
import br.com.andersondev.application.moderation.ResolveReportOutput;
import br.com.andersondev.application.moderation.create.CreateReportCommand;
import br.com.andersondev.application.moderation.create.CreateReportUseCase;
import br.com.andersondev.application.moderation.list.ListReportsCommand;
import br.com.andersondev.application.moderation.list.ListReportsUseCase;
import br.com.andersondev.application.moderation.resolve.ResolveReportCommand;
import br.com.andersondev.application.moderation.resolve.ResolveReportUseCase;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fronteiras transacionais dos casos de uso de Moderacao.
 * Os casos de uso sao Java puro (application layer), sem Spring.
 */
@Service
public class ModerationTransactionalFacade {

    private final CreateReportUseCase createReportUseCase;
    private final ListReportsUseCase listReportsUseCase;
    private final ResolveReportUseCase resolveReportUseCase;

    public ModerationTransactionalFacade(
            final CreateReportUseCase createReportUseCase,
            final ListReportsUseCase listReportsUseCase,
            final ResolveReportUseCase resolveReportUseCase
    ) {
        this.createReportUseCase = createReportUseCase;
        this.listReportsUseCase = listReportsUseCase;
        this.resolveReportUseCase = resolveReportUseCase;
    }

    @Transactional
    public ReportOutput createReport(final CreateReportCommand command) {
        return this.createReportUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public Pagination<ReportOutput> listReports(final ListReportsCommand command) {
        return this.listReportsUseCase.execute(command);
    }

    @Transactional
    public ResolveReportOutput resolveReport(final ResolveReportCommand command) {
        return this.resolveReportUseCase.execute(command);
    }
}
