package br.com.andersondev.application.game.delete;

import br.com.andersondev.application.UnitUseCase;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;

import java.util.Objects;

/**
 * Remocao de jogo via soft delete (P-013).
 * Jogo ja deletado e tratado como inexistente (404), evitando dupla delecao.
 */
public final class DefaultDeleteGameUseCase extends DeleteGameUseCase {

    private final GameGateway gameGateway;

    public DefaultDeleteGameUseCase(final GameGateway gameGateway) {
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public void execute(final DeleteGameCommand command) {
        final var gameId = GameId.from(command.id());
        final var game = this.gameGateway.findById(gameId)
                .filter(g -> !g.isDeleted())
                .orElseThrow(() -> EntityNotFoundException.with(Game.class, gameId));

        game.delete();
        this.gameGateway.save(game);
    }
}
