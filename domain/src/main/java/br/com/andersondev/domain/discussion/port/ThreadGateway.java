package br.com.andersondev.domain.discussion.port;

import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shared.Pagination;

import java.util.Optional;

/**
 * Contrato de persistencia do agregado Thread.
 */
public interface ThreadGateway {

    Thread save(Thread thread);

    Optional<Thread> findById(ThreadId id);

    Pagination<Thread> findByGameId(GameId gameId, int page, int size);
}
