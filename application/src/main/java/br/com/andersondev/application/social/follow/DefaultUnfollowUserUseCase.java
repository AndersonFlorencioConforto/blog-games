package br.com.andersondev.application.social.follow;

import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Deixar de seguir um usuario (DELETE /users/{id}/follow).
 * Idempotente: deixar de seguir quem nao se segue nao gera erro (retorna 204).
 */
public final class DefaultUnfollowUserUseCase extends UnfollowUserUseCase {

    private final FollowGateway followGateway;

    public DefaultUnfollowUserUseCase(final FollowGateway followGateway) {
        this.followGateway = Objects.requireNonNull(followGateway);
    }

    @Override
    public void execute(final FollowUserCommand command) {
        final var followerId = UserId.from(command.followerId());
        final var followedId = UserId.from(command.followedId());
        this.followGateway.deleteByFollowerIdAndFollowedId(followerId, followedId);
    }
}
