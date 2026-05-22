package br.com.andersondev.application.social.follow;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.social.Follow;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;

import java.util.Objects;

/**
 * Seguir um usuario (POST /users/{id}/follow). F-01..F-03.
 * Fluxo:
 *  1. Cria o agregado Follow, que rejeita auto-follow (F-01 -&gt; SelfReferenceException / 400).
 *  2. Verifica que o alvo existe (F-03 -&gt; 404).
 *  3. Idempotente: se ja segue, nao faz nada (F-02). Caso contrario, persiste.
 */
public final class DefaultFollowUserUseCase extends FollowUserUseCase {

    private final FollowGateway followGateway;
    private final UserGateway userGateway;

    public DefaultFollowUserUseCase(final FollowGateway followGateway, final UserGateway userGateway) {
        this.followGateway = Objects.requireNonNull(followGateway);
        this.userGateway = Objects.requireNonNull(userGateway);
    }

    @Override
    public void execute(final FollowUserCommand command) {
        final var followerId = UserId.from(command.followerId());
        final var followedId = UserId.from(command.followedId());

        // Invariante de auto-follow validada na criacao do agregado (F-01 / 400).
        final var follow = Follow.newFollow(followerId, followedId);

        if (this.userGateway.findById(followedId).isEmpty()) {
            throw EntityNotFoundException.with(User.class, followedId);
        }

        // Idempotencia (F-02): seguir quem ja se segue nao gera erro.
        if (this.followGateway.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            return;
        }

        this.followGateway.save(follow);
    }
}
