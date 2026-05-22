package br.com.andersondev.application.social.follow;

import br.com.andersondev.application.social.UserSummaryOutput;
import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Lista paginada de usuarios que um usuario segue (GET /users/{id}/following, publico).
 * Normaliza paginacao: page &gt;= 0, size entre 1 e 50 (data-strategy secao 3).
 */
public final class DefaultListFollowingUseCase extends ListFollowingUseCase {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final FollowGateway followGateway;

    public DefaultListFollowingUseCase(final FollowGateway followGateway) {
        this.followGateway = Objects.requireNonNull(followGateway);
    }

    @Override
    public Pagination<UserSummaryOutput> execute(final ListSocialCommand command) {
        final var userId = UserId.from(command.userId());
        final var page = Math.max(command.page(), 0);
        final var size = normalizeSize(command.size());
        return this.followGateway.findFollowing(userId, page, size).map(UserSummaryOutput::from);
    }

    private static int normalizeSize(final int requested) {
        if (requested <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(requested, MAX_SIZE);
    }
}
