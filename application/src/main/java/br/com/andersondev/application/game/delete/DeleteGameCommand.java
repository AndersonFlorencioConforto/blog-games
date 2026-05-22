package br.com.andersondev.application.game.delete;

/**
 * Comando de remocao (soft delete) de jogo (DELETE /games/{id}).
 */
public record DeleteGameCommand(String id) {

    public static DeleteGameCommand with(final String id) {
        return new DeleteGameCommand(id);
    }
}
