package br.com.andersondev.application.auth.password;

import br.com.andersondev.application.auth.port.ResetTokenProvider;
import br.com.andersondev.domain.auth.PasswordResetToken;
import br.com.andersondev.domain.auth.port.PasswordResetTokenGateway;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.exception.TokenAlreadyUsedException;
import br.com.andersondev.domain.exception.TokenExpiredException;
import br.com.andersondev.domain.user.PasswordPolicy;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.port.PasswordHasherPort;
import br.com.andersondev.domain.user.port.UserGateway;
import br.com.andersondev.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.Objects;

/**
 * Redefinicao de senha (P-02..P-05).
 * Fluxo:
 *  1. Valida nova senha (politica) -> 422.
 *  2. Hash do token -> lookup; expirado -> 400 (TokenExpired), ja usado -> 400 (TokenAlreadyUsed).
 *  3. Atualiza hash da senha e marca token como usado.
 *  4. Revoga TODOS os refresh tokens do usuario (P-05).
 */
public final class DefaultResetPasswordUseCase extends ResetPasswordUseCase {

    private final PasswordResetTokenGateway passwordResetTokenGateway;
    private final UserGateway userGateway;
    private final PasswordHasherPort passwordHasher;
    private final RefreshTokenGateway refreshTokenGateway;
    private final ResetTokenProvider resetTokenProvider;

    public DefaultResetPasswordUseCase(
            final PasswordResetTokenGateway passwordResetTokenGateway,
            final UserGateway userGateway,
            final PasswordHasherPort passwordHasher,
            final RefreshTokenGateway refreshTokenGateway,
            final ResetTokenProvider resetTokenProvider
    ) {
        this.passwordResetTokenGateway = Objects.requireNonNull(passwordResetTokenGateway);
        this.userGateway = Objects.requireNonNull(userGateway);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.refreshTokenGateway = Objects.requireNonNull(refreshTokenGateway);
        this.resetTokenProvider = Objects.requireNonNull(resetTokenProvider);
    }

    @Override
    public void execute(final ResetPasswordCommand command) {
        validateNewPassword(command.newPassword());

        if (command.token() == null || command.token().isBlank()) {
            throw TokenAlreadyUsedException.create();
        }

        final var tokenHash = this.resetTokenProvider.hash(command.token());
        final PasswordResetToken resetToken = this.passwordResetTokenGateway.findByTokenHash(tokenHash)
                .orElseThrow(TokenAlreadyUsedException::create);

        if (resetToken.isUsed()) {
            throw TokenAlreadyUsedException.create();
        }
        if (resetToken.isExpired(Instant.now())) {
            throw TokenExpiredException.with("Token de recuperacao expirado");
        }

        final User user = this.userGateway.findById(resetToken.getUserId())
                .orElseThrow(() -> EntityNotFoundException.with(User.class, resetToken.getUserId()));

        user.changePasswordHash(this.passwordHasher.hash(command.newPassword()));
        this.userGateway.save(user);

        resetToken.markUsed();
        this.passwordResetTokenGateway.save(resetToken);

        this.refreshTokenGateway.revokeAllByUserId(user.getId());
    }

    private void validateNewPassword(final String newPassword) {
        final var notification = Notification.create();
        notification.validate(() -> {
            PasswordPolicy.validate(newPassword);
            return null;
        });
        if (notification.hasError()) {
            throw new NotificationException("Senha nao atende a politica", notification);
        }
    }
}
