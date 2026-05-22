package br.com.andersondev.application.auth.login;

import br.com.andersondev.application.auth.AuthTokensOutput;
import br.com.andersondev.application.auth.port.AccessTokenProvider;
import br.com.andersondev.application.auth.port.RefreshTokenProvider;
import br.com.andersondev.domain.auth.RefreshToken;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.exception.InvalidCredentialsException;
import br.com.andersondev.domain.exception.UserBannedException;
import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.port.PasswordHasherPort;
import br.com.andersondev.domain.user.port.UserGateway;

import java.time.Instant;
import java.util.Objects;

/**
 * Login (A-01..A-03).
 * Fluxo:
 *  1. Resolve usuario por email; credenciais invalidas -> 401 (sem revelar campo, A-02).
 *  2. Verifica senha (BCrypt).
 *  3. Verifica ban (P-010 / A-03 -> 403).
 *  4. Emite access JWT (15min) + refresh token (7d) e persiste hash do refresh.
 */
public final class DefaultLoginUseCase extends LoginUseCase {

    private final UserGateway userGateway;
    private final PasswordHasherPort passwordHasher;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenGateway refreshTokenGateway;

    public DefaultLoginUseCase(
            final UserGateway userGateway,
            final PasswordHasherPort passwordHasher,
            final AccessTokenProvider accessTokenProvider,
            final RefreshTokenProvider refreshTokenProvider,
            final RefreshTokenGateway refreshTokenGateway
    ) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.accessTokenProvider = Objects.requireNonNull(accessTokenProvider);
        this.refreshTokenProvider = Objects.requireNonNull(refreshTokenProvider);
        this.refreshTokenGateway = Objects.requireNonNull(refreshTokenGateway);
    }

    @Override
    public AuthTokensOutput execute(final LoginCommand command) {
        final var user = resolveUser(command.email());

        if (!this.passwordHasher.matches(command.password(), user.getPasswordHash())) {
            throw InvalidCredentialsException.create();
        }

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

    private User resolveUser(final String rawEmail) {
        final Email email;
        try {
            email = Email.of(rawEmail);
        } catch (final RuntimeException ex) {
            // Email malformado nao revela detalhe; trata como credencial invalida.
            throw InvalidCredentialsException.create();
        }
        return this.userGateway.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::create);
    }
}
