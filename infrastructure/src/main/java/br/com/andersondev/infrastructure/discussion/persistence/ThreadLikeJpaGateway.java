package br.com.andersondev.infrastructure.discussion.persistence;

import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.ThreadLike;
import br.com.andersondev.domain.discussion.port.ThreadLikeGateway;
import br.com.andersondev.domain.user.UserId;
import org.springframework.stereotype.Component;

@Component
public class ThreadLikeJpaGateway implements ThreadLikeGateway {

    private final ThreadLikeRepository repository;

    public ThreadLikeJpaGateway(final ThreadLikeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(final ThreadLike threadLike) {
        this.repository.save(ThreadLikeJpaEntity.from(threadLike));
    }

    @Override
    public boolean existsByThreadIdAndUserId(final ThreadId threadId, final UserId userId) {
        return this.repository.existsByIdThreadIdAndIdUserId(threadId.getValue(), userId.getValue());
    }

    @Override
    public void deleteByThreadIdAndUserId(final ThreadId threadId, final UserId userId) {
        this.repository.deleteByIdThreadIdAndIdUserId(threadId.getValue(), userId.getValue());
    }
}
