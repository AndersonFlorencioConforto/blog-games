package br.com.andersondev.application.rating.rate;

/**
 * Comando para avaliar um jogo (POST /games/{id}/ratings).
 * {@code userId} vem do contexto autenticado.
 */
public record RateGameCommand(String gameId, String userId, int stars) {

    public static RateGameCommand with(final String gameId, final String userId, final int stars) {
        return new RateGameCommand(gameId, userId, stars);
    }
}
