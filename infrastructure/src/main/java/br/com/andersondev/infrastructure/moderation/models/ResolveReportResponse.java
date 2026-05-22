package br.com.andersondev.infrastructure.moderation.models;

import br.com.andersondev.application.moderation.ResolveReportOutput;

import java.time.Instant;

/**
 * Resposta HTTP apos resolucao de uma denuncia.
 */
public record ResolveReportResponse(
        String id,
        String status,
        String adminNote,
        String resolvedBy,
        Instant resolvedAt
) {

    public static ResolveReportResponse from(final ResolveReportOutput output) {
        return new ResolveReportResponse(
                output.id(),
                output.status(),
                output.adminNote(),
                output.resolvedBy(),
                output.resolvedAt()
        );
    }
}
