package br.com.andersondev.application.discussion.thread.list;

import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shared.Pagination;

import java.util.Objects;

/**
 * Lista threads de um jogo paginadas (GET /games/{gameId}/threads).
 */
public final class DefaultListThreadsByGameUseCase extends ListThreadsByGameUseCase {

    private final ThreadGateway threadGateway;

    public DefaultListThreadsByGameUseCase(final ThreadGateway threadGateway) {
        this.threadGateway = Objects.requireNonNull(threadGateway);
    }

    @Override
    public Pagination<ThreadSummaryOutput> execute(final ListThreadsByGameCommand command) {
        final var gameId = GameId.from(command.gameId());
        return this.threadGateway
                .findByGameId(gameId, command.page(), command.size())
                .map(ThreadSummaryOutput::from);
    }
}
