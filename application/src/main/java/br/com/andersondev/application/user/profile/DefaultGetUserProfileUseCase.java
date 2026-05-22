package br.com.andersondev.application.user.profile;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;

import java.util.Objects;

/**
 * Perfil publico de um usuario (GET /users/{id}). 404 se nao existe.
 * Contadores de seguidores/seguindo derivados do FollowGateway.
 */
public final class DefaultGetUserProfileUseCase extends GetUserProfileUseCase {

    private final UserGateway userGateway;
    private final FollowGateway followGateway;

    public DefaultGetUserProfileUseCase(final UserGateway userGateway, final FollowGateway followGateway) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.followGateway = Objects.requireNonNull(followGateway);
    }

    @Override
    public UserProfileOutput execute(final String id) {
        final var userId = UserId.from(id);
        final var user = this.userGateway.findById(userId)
                .orElseThrow(() -> EntityNotFoundException.with(User.class, userId));

        final var followers = this.followGateway.countByFollowedId(userId);
        final var following = this.followGateway.countByFollowerId(userId);
        return UserProfileOutput.from(user, followers, following);
    }
}
