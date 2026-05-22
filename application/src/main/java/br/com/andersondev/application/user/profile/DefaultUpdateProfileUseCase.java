package br.com.andersondev.application.user.profile;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;
import br.com.andersondev.domain.validation.handler.Notification;

import java.util.Objects;

/**
 * Atualiza o perfil do usuario autenticado (PUT /users/me). PR-01..PR-04.
 * Fluxo:
 *  1. Carrega o proprio usuario (id do principal). 404 se inexistente.
 *  2. Aplica updateProfile e valida invariantes (name 2-100, bio &lt;=500, avatarUrl URL valida).
 *  3. 422 (NotificationException) em dados invalidos; persiste caso valido.
 * Ownership e garantido porque o id vem do contexto autenticado (PR-04).
 */
public final class DefaultUpdateProfileUseCase extends UpdateProfileUseCase {

    private final UserGateway userGateway;
    private final FollowGateway followGateway;

    public DefaultUpdateProfileUseCase(final UserGateway userGateway, final FollowGateway followGateway) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.followGateway = Objects.requireNonNull(followGateway);
    }

    @Override
    public UserProfileOutput execute(final UpdateProfileCommand command) {
        final var userId = UserId.from(command.userId());
        final var user = this.userGateway.findById(userId)
                .orElseThrow(() -> EntityNotFoundException.with(User.class, userId));

        user.updateProfile(command.name(), command.bio(), command.avatarUrl());

        final var notification = Notification.create();
        user.validate(notification);
        if (notification.hasError()) {
            throw new NotificationException("Nao foi possivel atualizar o perfil", notification);
        }

        final var saved = this.userGateway.save(user);
        final var followers = this.followGateway.countByFollowedId(userId);
        final var following = this.followGateway.countByFollowerId(userId);
        return UserProfileOutput.from(saved, followers, following);
    }
}
