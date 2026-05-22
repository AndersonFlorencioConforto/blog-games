package br.com.andersondev.application.moderation.list;

import br.com.andersondev.application.moderation.ReportOutput;
import br.com.andersondev.domain.moderation.ReportStatus;
import br.com.andersondev.domain.moderation.port.ReportGateway;
import br.com.andersondev.domain.shared.Pagination;

import java.util.Objects;

/**
 * Lista denuncias paginadas com filtro opcional de status.
 */
public final class DefaultListReportsUseCase extends ListReportsUseCase {

    private final ReportGateway reportGateway;

    public DefaultListReportsUseCase(final ReportGateway reportGateway) {
        this.reportGateway = Objects.requireNonNull(reportGateway);
    }

    @Override
    public Pagination<ReportOutput> execute(final ListReportsCommand command) {
        final ReportStatus status = command.status() != null
                ? ReportStatus.valueOf(command.status())
                : null;
        return this.reportGateway.findAll(status, command.page(), command.size())
                .map(ReportOutput::from);
    }
}
