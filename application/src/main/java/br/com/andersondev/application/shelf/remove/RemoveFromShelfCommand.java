package br.com.andersondev.application.shelf.remove;

/**
 * Comando para remover um jogo da propria estante (DELETE /shelf/{gameId}).
 * {@code userId} vem sempre do contexto autenticado (dono da estante).
 */
public record RemoveFromShelfCommand(String userId, String gameId) {

    public static RemoveFromShelfCommand with(final String userId, final String gameId) {
        return new RemoveFromShelfCommand(userId, gameId);
    }
}
