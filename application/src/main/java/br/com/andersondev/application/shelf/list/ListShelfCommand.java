package br.com.andersondev.application.shelf.list;

/**
 * Comando de listagem paginada da estante de um usuario (GET /users/{userId}/shelf, publico).
 * {@code status} e filtro opcional (null/blank = todos).
 */
public record ListShelfCommand(String userId, String status, int page, int size) {

    public static ListShelfCommand with(final String userId, final String status, final int page, final int size) {
        return new ListShelfCommand(userId, status, page, size);
    }
}
