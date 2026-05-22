package br.com.andersondev.infrastructure.moderation.models;

/**
 * Corpo da requisicao para resolver uma denuncia.
 */
public record ResolveReportRequest(
        String decision,
        String adminNote,
        Integer banDurationDays
) {
}
