package br.com.andersondev.infrastructure.discussion.persistence;

import br.com.andersondev.domain.discussion.ReplyId;
import br.com.andersondev.domain.discussion.ReplyLike;
import br.com.andersondev.domain.discussion.port.ReplyLikeGateway;
import br.com.andersondev.domain.user.UserId;
import org.springframework.stereotype.Component;

@Component
public class ReplyLikeJpaGateway implements ReplyLikeGateway {

    private final ReplyLikeRepository repository;

    public ReplyLikeJpaGateway(final ReplyLikeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(final ReplyLike replyLike) {
        this.repository.save(ReplyLikeJpaEntity.from(replyLike));
    }

    @Override
    public boolean existsByReplyIdAndUserId(final ReplyId replyId, final UserId userId) {
        return this.repository.existsByIdReplyIdAndIdUserId(replyId.getValue(), userId.getValue());
    }

    @Override
    public void deleteByReplyIdAndUserId(final ReplyId replyId, final UserId userId) {
        this.repository.deleteByIdReplyIdAndIdUserId(replyId.getValue(), userId.getValue());
    }
}
