package br.com.andersondev.application.moderation.create;

/**
 * Comando para criar uma nova denuncia.
 */
public record CreateReportCommand(
        String reportedUserId,
        String reportedById,
        String reason,
        String description
) {

    public static CreateReportCommand with(
            final String reportedUserId,
            final String reportedById,
            final String reason,
            final String description
    ) {
        return new CreateReportCommand(reportedUserId, reportedById, reason, description);
    }
}
