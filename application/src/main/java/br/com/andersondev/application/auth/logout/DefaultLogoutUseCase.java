package br.com.andersondev.application.auth.logout;

import br.com.andersondev.application.auth.port.RefreshTokenProvider;
import br.com.andersondev.domain.auth.BlocklistedToken;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.auth.port.TokenBlocklistGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Logout (transaction-boundaries / LogoutUseCase).
 * Fluxo (transacao unica):
 *  1. Insere jti do access token na denylist (ate sua expiracao natural).
 *  2. Revoga o refresh token enviado (ou todos do usuario se nenhum informado).
 */
public final class DefaultLogoutUseCase extends LogoutUseCase {

    private final TokenBlocklistGateway tokenBlocklistGateway;
    private final RefreshTokenGateway refreshTokenGateway;
    private final RefreshTokenProvider refreshTokenProvider;

    public DefaultLogoutUseCase(
            final TokenBlocklistGateway tokenBlocklistGateway,
            final RefreshTokenGateway refreshTokenGateway,
            final RefreshTokenProvider refreshTokenProvider
    ) {
        this.tokenBlocklistGateway = Objects.requireNonNull(tokenBlocklistGateway);
        this.refreshTokenGateway = Objects.requireNonNull(refreshTokenGateway);
        this.refreshTokenProvider = Objects.requireNonNull(refreshTokenProvider);
    }

    @Override
    public void execute(final LogoutCommand command) {
        if (command.jti() != null && command.accessTokenExpiresAt() != null) {
            this.tokenBlocklistGateway.save(
                    BlocklistedToken.of(command.jti(), command.accessTokenExpiresAt()));
        }

        if (command.refreshToken() != null && !command.refreshToken().isBlank()) {
            final var tokenHash = this.refreshTokenProvider.hash(command.refreshToken());
            this.refreshTokenGateway.findByTokenHash(tokenHash).ifPresent(token -> {
                token.revoke();
                this.refreshTokenGateway.save(token);
            });
        } else if (command.userId() != null) {
            this.refreshTokenGateway.revokeAllByUserId(UserId.from(command.userId()));
        }
    }
}
