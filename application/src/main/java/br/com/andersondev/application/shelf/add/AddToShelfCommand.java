package br.com.andersondev.application.shelf.add;

/**
 * Comando para adicionar um jogo a propria estante (POST /shelf).
 * {@code userId} vem sempre do contexto autenticado (dono da estante).
 */
public record AddToShelfCommand(String userId, String gameId, String status) {

    public static AddToShelfCommand with(final String userId, final String gameId, final String status) {
        return new AddToShelfCommand(userId, gameId, status);
    }
}
