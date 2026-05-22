package br.com.andersondev.application.moderation;

import br.com.andersondev.domain.moderation.Report;

import java.time.Instant;

/**
 * Output retornado apos resolucao de uma denuncia.
 */
public record ResolveReportOutput(
        String id,
        String status,
        String adminNote,
        String resolvedBy,
        Instant resolvedAt
) {

    public static ResolveReportOutput from(final Report report) {
        return new ResolveReportOutput(
                report.getId().getValue(),
                report.getStatus().name(),
                report.getAdminNote(),
                report.getResolvedBy() != null ? report.getResolvedBy().getValue() : null,
                report.getResolvedAt()
        );
    }
}
