package br.com.andersondev.domain.game;

/**
 * Projecao resumida de jogo para listagens (ex.: estante).
 * Espelha o contrato HTTP: {@code id, title, coverImageUrl} (api-catalog secao 4).
 */
public record GameSummary(String id, String title, String coverImageUrl) {

    public static GameSummary of(final String id, final String title, final String coverImageUrl) {
        return new GameSummary(id, title, coverImageUrl);
    }

    public static GameSummary from(final Game game) {
        return new GameSummary(game.getId().getValue(), game.getTitle(), game.getCoverImageUrl());
    }
}
