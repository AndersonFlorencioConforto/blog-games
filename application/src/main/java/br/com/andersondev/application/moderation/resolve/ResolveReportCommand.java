package br.com.andersondev.application.moderation.resolve;

/**
 * Comando para resolver (aprovar ou rejeitar) uma denuncia.
 */
public record ResolveReportCommand(
        String reportId,
        String decision,
        String adminNote,
        Integer banDurationDays,
        String resolvedByUserId
) {

    public static ResolveReportCommand with(
            final String reportId,
            final String decision,
            final String adminNote,
            final Integer banDurationDays,
            final String resolvedByUserId
    ) {
        return new ResolveReportCommand(reportId, decision, adminNote, banDurationDays, resolvedByUserId);
    }
}
