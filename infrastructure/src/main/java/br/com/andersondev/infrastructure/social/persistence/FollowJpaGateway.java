package br.com.andersondev.infrastructure.social.persistence;

import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.social.Follow;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.UserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class FollowJpaGateway implements FollowGateway {

    private final FollowRepository repository;

    public FollowJpaGateway(final FollowRepository repository) {
        this.repository = repository;
    }

    @Override
    public Follow save(final Follow follow) {
        return this.repository.save(FollowJpaEntity.from(follow)).toAggregate();
    }

    @Override
    public boolean existsByFollowerIdAndFollowedId(final UserId followerId, final UserId followedId) {
        return this.repository.existsByFollowerIdAndFollowedId(followerId.getValue(), followedId.getValue());
    }

    @Override
    public void deleteByFollowerIdAndFollowedId(final UserId followerId, final UserId followedId) {
        this.repository.deleteByFollowerIdAndFollowedId(followerId.getValue(), followedId.getValue());
    }

    @Override
    public long countByFollowedId(final UserId followedId) {
        return this.repository.countByFollowedId(followedId.getValue());
    }

    @Override
    public long countByFollowerId(final UserId followerId) {
        return this.repository.countByFollowerId(followerId.getValue());
    }

    @Override
    public Pagination<UserSummary> findFollowers(final UserId followedId, final int page, final int size) {
        final var result = this.repository.findFollowers(followedId.getValue(), PageRequest.of(page, size));
        return toPagination(result);
    }

    @Override
    public Pagination<UserSummary> findFollowing(final UserId followerId, final int page, final int size) {
        final var result = this.repository.findFollowing(followerId.getValue(), PageRequest.of(page, size));
        return toPagination(result);
    }

    private static Pagination<UserSummary> toPagination(final Page<UserSummaryProjection> page) {
        final var items = page.getContent().stream()
                .map(p -> UserSummary.of(p.id(), p.name(), p.avatarUrl()))
                .toList();
        return new Pagination<>(page.getNumber(), page.getSize(), page.getTotalElements(), items);
    }
}
