package br.com.andersondev.application.discussion.thread.create;

import br.com.andersondev.application.discussion.thread.ThreadOutput;
import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Cria uma nova thread de discussao (POST /games/{gameId}/threads).
 * Fluxo:
 *  1. Verifica que o jogo existe e nao esta deletado (404 caso contrario).
 *  2. Cria o agregado Thread e persiste.
 */
public final class DefaultCreateThreadUseCase extends CreateThreadUseCase {

    private final ThreadGateway threadGateway;
    private final GameGateway gameGateway;

    public DefaultCreateThreadUseCase(final ThreadGateway threadGateway, final GameGateway gameGateway) {
        this.threadGateway = Objects.requireNonNull(threadGateway);
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public ThreadOutput execute(final CreateThreadCommand command) {
        final var gameId = GameId.from(command.gameId());
        final var authorId = UserId.from(command.authorId());

        final var gameExists = this.gameGateway.findById(gameId)
                .filter(game -> !game.isDeleted())
                .isPresent();
        if (!gameExists) {
            throw EntityNotFoundException.with(Game.class, gameId);
        }

        final var thread = Thread.newThread(gameId, authorId, command.title(), command.content());
        return ThreadOutput.from(this.threadGateway.save(thread));
    }
}
