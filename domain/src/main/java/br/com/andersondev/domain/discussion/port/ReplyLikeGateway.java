package br.com.andersondev.domain.discussion.port;

import br.com.andersondev.domain.discussion.ReplyId;
import br.com.andersondev.domain.discussion.ReplyLike;
import br.com.andersondev.domain.user.UserId;

/**
 * Contrato de persistencia de ReplyLike.
 */
public interface ReplyLikeGateway {

    void save(ReplyLike replyLike);

    boolean existsByReplyIdAndUserId(ReplyId replyId, UserId userId);

    void deleteByReplyIdAndUserId(ReplyId replyId, UserId userId);
}
