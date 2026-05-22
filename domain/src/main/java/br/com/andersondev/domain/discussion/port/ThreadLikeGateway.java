package br.com.andersondev.domain.discussion.port;

import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.ThreadLike;
import br.com.andersondev.domain.user.UserId;

/**
 * Contrato de persistencia de ThreadLike.
 */
public interface ThreadLikeGateway {

    void save(ThreadLike threadLike);

    boolean existsByThreadIdAndUserId(ThreadId threadId, UserId userId);

    void deleteByThreadIdAndUserId(ThreadId threadId, UserId userId);
}
