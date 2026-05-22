package br.com.andersondev.application.moderation.list;

/**
 * Comando para listar denuncias com filtro opcional de status.
 */
public record ListReportsCommand(
        String status,
        int page,
        int size
) {

    public static ListReportsCommand with(final String status, final int page, final int size) {
        return new ListReportsCommand(status, page, size);
    }
}
