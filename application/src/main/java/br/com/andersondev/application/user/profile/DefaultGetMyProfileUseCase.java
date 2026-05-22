package br.com.andersondev.application.user.profile;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;

import java.util.Objects;

/**
 * Dados do usuario autenticado (GET /users/me), incluindo email e role.
 * O id vem do principal autenticado. 404 caso o usuario nao exista mais.
 */
public final class DefaultGetMyProfileUseCase extends GetMyProfileUseCase {

    private final UserGateway userGateway;
    private final FollowGateway followGateway;

    public DefaultGetMyProfileUseCase(final UserGateway userGateway, final FollowGateway followGateway) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.followGateway = Objects.requireNonNull(followGateway);
    }

    @Override
    public MyProfileOutput execute(final String authenticatedUserId) {
        final var userId = UserId.from(authenticatedUserId);
        final var user = this.userGateway.findById(userId)
                .orElseThrow(() -> EntityNotFoundException.with(User.class, userId));

        final var followers = this.followGateway.countByFollowedId(userId);
        final var following = this.followGateway.countByFollowerId(userId);
        return MyProfileOutput.from(user, followers, following);
    }
}
