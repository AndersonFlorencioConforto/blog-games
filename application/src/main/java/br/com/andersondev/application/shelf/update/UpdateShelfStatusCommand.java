package br.com.andersondev.application.shelf.update;

/**
 * Comando para atualizar o status de um jogo na propria estante (PUT /shelf/{gameId}).
 * {@code userId} vem sempre do contexto autenticado (dono da estante).
 */
public record UpdateShelfStatusCommand(String userId, String gameId, String status) {

    public static UpdateShelfStatusCommand with(final String userId, final String gameId, final String status) {
        return new UpdateShelfStatusCommand(userId, gameId, status);
    }
}
