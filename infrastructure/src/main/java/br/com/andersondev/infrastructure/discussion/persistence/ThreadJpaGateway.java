package br.com.andersondev.infrastructure.discussion.persistence;

import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ThreadJpaGateway implements ThreadGateway {

    private final ThreadRepository repository;

    public ThreadJpaGateway(final ThreadRepository repository) {
        this.repository = repository;
    }

    @Override
    public Thread save(final Thread thread) {
        return this.repository.save(ThreadJpaEntity.from(thread)).toAggregate();
    }

    @Override
    public Optional<Thread> findById(final ThreadId id) {
        return this.repository.findById(id.getValue()).map(ThreadJpaEntity::toAggregate);
    }

    @Override
    public Pagination<Thread> findByGameId(final GameId gameId, final int page, final int size) {
        final var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        final var result = this.repository.findByGameId(gameId.getValue(), pageable);
        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent().stream().map(ThreadJpaEntity::toAggregate).toList()
        );
    }
}
