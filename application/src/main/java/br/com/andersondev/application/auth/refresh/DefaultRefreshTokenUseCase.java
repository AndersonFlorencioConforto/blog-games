package br.com.andersondev.application.auth.refresh;

import br.com.andersondev.application.auth.AuthTokensOutput;
import br.com.andersondev.application.auth.port.AccessTokenProvider;
import br.com.andersondev.application.auth.port.RefreshTokenProvider;
import br.com.andersondev.domain.auth.RefreshToken;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.TokenExpiredException;
import br.com.andersondev.domain.exception.UserBannedException;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.port.UserGateway;

import java.time.Instant;
import java.util.Objects;

/**
 * Renovacao de tokens com rotacao obrigatoria (A-07, A-08).
 * Fluxo:
 *  1. Hash do refresh token recebido -> lookup.
 *  2. Valida expiracao/revogacao (-> 401 TokenExpired).
 *  3. Revoga o token atual (rotacao).
 *  4. Verifica ban do usuario.
 *  5. Emite novo par de tokens e persiste o novo refresh.
 */
public final class DefaultRefreshTokenUseCase extends RefreshTokenUseCase {

    private final RefreshTokenGateway refreshTokenGateway;
    private final UserGateway userGateway;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    public DefaultRefreshTokenUseCase(
            final RefreshTokenGateway refreshTokenGateway,
            final UserGateway userGateway,
            final AccessTokenProvider accessTokenProvider,
            final RefreshTokenProvider refreshTokenProvider
    ) {
        this.refreshTokenGateway = Objects.requireNonNull(refreshTokenGateway);
        this.userGateway = Objects.requireNonNull(userGateway);
        this.accessTokenProvider = Objects.requireNonNull(accessTokenProvider);
        this.refreshTokenProvider = Objects.requireNonNull(refreshTokenProvider);
    }

    @Override
    public AuthTokensOutput execute(final RefreshTokenCommand command) {
        if (command.refreshToken() == null || command.refreshToken().isBlank()) {
            throw TokenExpiredException.create();
        }

        final var tokenHash = this.refreshTokenProvider.hash(command.refreshToken());
        final var current = this.refreshTokenGateway.findByTokenHash(tokenHash)
                .orElseThrow(TokenExpiredException::create);

        if (!current.isUsable(Instant.now())) {
            throw TokenExpiredException.create();
        }

        // Rotacao: revoga o token atual.
        current.revoke();
        this.refreshTokenGateway.save(current);

        final User user = this.userGateway.findById(current.getUserId())
                .orElseThrow(() -> EntityNotFoundException.with(User.class, current.getUserId()));

        if (user.isBanned(Instant.now())) {
            throw UserBannedException.until(user.getBannedUntil());
        }

        final var accessToken = this.accessTokenProvider.generate(
                user.getId(), user.getEmail().getValue(), user.getRole());

        final var generatedRefresh = this.refreshTokenProvider.generate();
        this.refreshTokenGateway.save(RefreshToken.issue(
                user.getId(),
                generatedRefresh.tokenHash(),
                generatedRefresh.expiresAt(),
                command.deviceInfo()
        ));

        return AuthTokensOutput.of(
                accessToken.token(),
                generatedRefresh.rawValue(),
                accessToken.expiresInSeconds()
        );
    }
}
