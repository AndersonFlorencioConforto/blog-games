package br.com.andersondev.infrastructure.moderation.models;

import br.com.andersondev.application.moderation.ReportOutput;

import java.time.Instant;

/**
 * Resposta HTTP completa de uma denuncia.
 */
public record ReportResponse(
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

    public static ReportResponse from(final ReportOutput output) {
        return new ReportResponse(
                output.id(),
                output.reportedUserId(),
                output.reportedById(),
                output.reason(),
                output.description(),
                output.status(),
                output.adminNote(),
                output.resolvedBy(),
                output.resolvedAt(),
                output.createdAt()
        );
    }
}
