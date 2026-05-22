package br.com.andersondev.application.moderation.create;

import br.com.andersondev.application.moderation.ReportOutput;
import br.com.andersondev.domain.exception.BusinessRuleViolationException;
import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.moderation.Report;
import br.com.andersondev.domain.moderation.ReportReason;
import br.com.andersondev.domain.moderation.port.ReportGateway;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;
import br.com.andersondev.domain.validation.Error;

import java.util.Objects;

/**
 * Cria uma nova denuncia.
 * Fluxo:
 *  1. Valida que reportedById != reportedUserId.
 *  2. Verifica que o usuario denunciado existe.
 *  3. Verifica que nao existe denuncia PENDING do mesmo denunciante para o mesmo denunciado.
 *  4. Cria e persiste o Report.
 */
public final class DefaultCreateReportUseCase extends CreateReportUseCase {

    private final ReportGateway reportGateway;
    private final UserGateway userGateway;

    public DefaultCreateReportUseCase(
            final ReportGateway reportGateway,
            final UserGateway userGateway
    ) {
        this.reportGateway = Objects.requireNonNull(reportGateway);
        this.userGateway = Objects.requireNonNull(userGateway);
    }

    @Override
    public ReportOutput execute(final CreateReportCommand command) {
        final var reportedUserId = UserId.from(command.reportedUserId());
        final var reportedById = UserId.from(command.reportedById());

        if (reportedById.getValue().equals(reportedUserId.getValue())) {
            throw DomainException.with(new Error("Um usuario nao pode denunciar a si mesmo"));
        }

        this.userGateway.findById(reportedUserId)
                .orElseThrow(() -> EntityNotFoundException.with(User.class, reportedUserId));

        if (this.reportGateway.existsPendingReport(reportedById, reportedUserId)) {
            throw BusinessRuleViolationException.with(
                    "Voce ja possui uma denuncia pendente para este usuario");
        }

        final var reason = ReportReason.valueOf(command.reason());
        final var report = Report.newReport(reportedUserId, reportedById, reason, command.description());
        return ReportOutput.from(this.reportGateway.save(report));
    }
}
