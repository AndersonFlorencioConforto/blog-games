package br.com.andersondev.infrastructure.discussion.persistence;

import br.com.andersondev.domain.discussion.Reply;
import br.com.andersondev.domain.discussion.ReplyId;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.port.ReplyGateway;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReplyJpaGateway implements ReplyGateway {

    private final ReplyRepository repository;

    public ReplyJpaGateway(final ReplyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Reply save(final Reply reply) {
        return this.repository.save(ReplyJpaEntity.from(reply)).toAggregate();
    }

    @Override
    public Optional<Reply> findById(final ReplyId id) {
        return this.repository.findById(id.getValue()).map(ReplyJpaEntity::toAggregate);
    }

    @Override
    public Pagination<Reply> findByThreadId(final ThreadId threadId, final int page, final int size) {
        final var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        final var result = this.repository.findByThreadId(threadId.getValue(), pageable);
        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent().stream().map(ReplyJpaEntity::toAggregate).toList()
        );
    }
}
