package br.com.andersondev.application.game.get;

import br.com.andersondev.application.game.GameOutput;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;

import java.util.Objects;

/**
 * Detalhe de um jogo (GET /games/{id}). Jogos deletados (soft delete) sao tratados
 * como inexistentes (404), conforme a regra de leitura publica (G-09).
 */
public final class DefaultGetGameByIdUseCase extends GetGameByIdUseCase {

    private final GameGateway gameGateway;

    public DefaultGetGameByIdUseCase(final GameGateway gameGateway) {
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public GameOutput execute(final String id) {
        final var gameId = GameId.from(id);
        return this.gameGateway.findById(gameId)
                .filter(game -> !game.isDeleted())
                .map(GameOutput::from)
                .orElseThrow(() -> EntityNotFoundException.with(Game.class, gameId));
    }
}
