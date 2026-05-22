package br.com.andersondev.application.moderation.resolve;

import br.com.andersondev.application.moderation.ResolveReportOutput;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.moderation.Report;
import br.com.andersondev.domain.moderation.ReportId;
import br.com.andersondev.domain.moderation.ReportStatus;
import br.com.andersondev.domain.moderation.port.ReportGateway;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Resolve uma denuncia como APPROVED ou REJECTED.
 * Se APPROVED e banDurationDays > 0, bane o usuario denunciado.
 * Fluxo:
 *  1. Carrega a denuncia ou lanca NotFoundException.
 *  2. Chama report.resolve(...) — lanca DomainException se ja resolvida.
 *  3. Persiste a denuncia.
 *  4. Se APPROVED e banDurationDays != null e > 0: carrega o usuario e aplica ban.
 */
public final class DefaultResolveReportUseCase extends ResolveReportUseCase {

    private final ReportGateway reportGateway;
    private final UserGateway userGateway;

    public DefaultResolveReportUseCase(
            final ReportGateway reportGateway,
            final UserGateway userGateway
    ) {
        this.reportGateway = Objects.requireNonNull(reportGateway);
        this.userGateway = Objects.requireNonNull(userGateway);
    }

    @Override
    public ResolveReportOutput execute(final ResolveReportCommand command) {
        final var reportId = ReportId.from(command.reportId());
        final var report = this.reportGateway.findById(reportId)
                .orElseThrow(() -> EntityNotFoundException.with(Report.class, reportId));

        final var decision = ReportStatus.valueOf(command.decision());
        final var resolvedBy = UserId.from(command.resolvedByUserId());

        report.resolve(decision, command.adminNote(), resolvedBy);
        final var saved = this.reportGateway.save(report);

        if (decision == ReportStatus.APPROVED
                && command.banDurationDays() != null
                && command.banDurationDays() > 0) {
            final var reportedUserId = report.getReportedUserId();
            final var user = this.userGateway.findById(reportedUserId)
                    .orElseThrow(() -> EntityNotFoundException.with(User.class, reportedUserId));
            user.banUntil(Instant.now().plus(command.banDurationDays(), ChronoUnit.DAYS));
            this.userGateway.save(user);
        }

        return ResolveReportOutput.from(saved);
    }
}
