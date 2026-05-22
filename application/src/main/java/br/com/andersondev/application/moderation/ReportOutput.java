package br.com.andersondev.application.moderation;

import br.com.andersondev.domain.moderation.Report;

import java.time.Instant;

/**
 * Output completo de uma denuncia.
 */
public record ReportOutput(
        String id,
        String reportedUserId,
        String reportedById,
        String reason,
        String description,
        String status,
        String adminNote,
        String resolvedBy,
        Instant resolvedAt,
        Instant createdAt
) {

    public static ReportOutput from(final Report report) {
        return new ReportOutput(
                report.getId().getValue(),
                report.getReportedUserId().getValue(),
                report.getReportedById().getValue(),
                report.getReason().name(),
                report.getDescription(),
                report.getStatus().name(),
                report.getAdminNote(),
                report.getResolvedBy() != null ? report.getResolvedBy().getValue() : null,
                report.getResolvedAt(),
                report.getCreatedAt()
        );
    }
}
