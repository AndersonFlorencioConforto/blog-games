package br.com.andersondev.domain.discussion.port;

import br.com.andersondev.domain.discussion.Reply;
import br.com.andersondev.domain.discussion.ReplyId;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.shared.Pagination;

import java.util.Optional;

/**
 * Contrato de persistencia do agregado Reply.
 */
public interface ReplyGateway {

    Reply save(Reply reply);

    Optional<Reply> findById(ReplyId id);

    Pagination<Reply> findByThreadId(ThreadId threadId, int page, int size);
}
