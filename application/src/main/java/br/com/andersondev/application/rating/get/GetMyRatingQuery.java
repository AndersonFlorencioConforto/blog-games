package br.com.andersondev.application.rating.get;

/**
 * Consulta da avaliacao do usuario autenticado para um jogo (GET /games/{id}/ratings/me).
 */
public record GetMyRatingQuery(String gameId, String userId) {

    public static GetMyRatingQuery with(final String gameId, final String userId) {
        return new GetMyRatingQuery(gameId, userId);
    }
}
