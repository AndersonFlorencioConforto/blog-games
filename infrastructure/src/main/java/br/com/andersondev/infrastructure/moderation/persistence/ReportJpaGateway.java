package br.com.andersondev.infrastructure.moderation.persistence;

import br.com.andersondev.domain.moderation.Report;
import br.com.andersondev.domain.moderation.ReportId;
import br.com.andersondev.domain.moderation.ReportStatus;
import br.com.andersondev.domain.moderation.port.ReportGateway;
import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.user.UserId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter JPA que implementa ReportGateway.
 */
@Component
public class ReportJpaGateway implements ReportGateway {

    private final ReportRepository reportRepository;

    public ReportJpaGateway(final ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report save(final Report report) {
        return this.reportRepository.save(ReportJpaEntity.from(report)).toAggregate();
    }

    @Override
    public Optional<Report> findById(final ReportId id) {
        return this.reportRepository.findById(id.getValue())
                .map(ReportJpaEntity::toAggregate);
    }

    @Override
    public Pagination<Report> findAll(final ReportStatus status, final int page, final int size) {
        final var pageable = PageRequest.of(page, size);
        final var result = status != null
                ? this.reportRepository.findByStatus(status, pageable)
                : this.reportRepository.findAll(pageable);
        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent().stream().map(ReportJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public boolean existsPendingReport(final UserId reportedById, final UserId reportedUserId) {
        return this.reportRepository.existsByReportedByIdAndReportedUserIdAndStatus(
                reportedById.getValue(),
                reportedUserId.getValue(),
                ReportStatus.PENDING
        );
    }
}
