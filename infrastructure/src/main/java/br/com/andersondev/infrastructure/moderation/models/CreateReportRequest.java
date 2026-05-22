package br.com.andersondev.infrastructure.moderation.models;

/**
 * Corpo da requisicao para criar uma denuncia.
 */
public record CreateReportRequest(
        String reason,
        String description
) {
}
