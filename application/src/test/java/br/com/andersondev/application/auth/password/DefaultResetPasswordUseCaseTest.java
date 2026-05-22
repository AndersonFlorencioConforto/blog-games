package br.com.andersondev.application.auth.password;

import br.com.andersondev.application.auth.port.ResetTokenProvider;
import br.com.andersondev.domain.auth.PasswordResetToken;
import br.com.andersondev.domain.auth.port.PasswordResetTokenGateway;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.exception.TokenAlreadyUsedException;
import br.com.andersondev.domain.exception.TokenExpiredException;
import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.PasswordHasherPort;
import br.com.andersondev.domain.user.port.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultResetPasswordUseCaseTest {

    @Mock
    private PasswordResetTokenGateway passwordResetTokenGateway;
    @Mock
    private UserGateway userGateway;
    @Mock
    private PasswordHasherPort passwordHasher;
    @Mock
    private RefreshTokenGateway refreshTokenGateway;
    @Mock
    private ResetTokenProvider resetTokenProvider;

    @InjectMocks
    private DefaultResetPasswordUseCase useCase;

    private final UserId userId = UserId.unique();

    private User user() {
        return User.with(userId, "Anderson", Email.of("anderson@email.com"),
                "old-hash", Role.USER, null, null, null, true, Instant.now(), Instant.now());
    }

    @Test
    void givenValidTokenAndStrongPassword_whenExecute_thenUpdatesPasswordAndRevokesTokens() {
        final var token = PasswordResetToken.issue(userId, "thash", Instant.now().plusSeconds(3600));
        when(resetTokenProvider.hash(eq("raw"))).thenReturn("thash");
        when(passwordResetTokenGateway.findByTokenHash("thash")).thenReturn(Optional.of(token));
        when(userGateway.findById(userId)).thenReturn(Optional.of(user()));
        when(passwordHasher.hash("NovaSenha@456")).thenReturn("new-hash");

        useCase.execute(ResetPasswordCommand.with("raw", "NovaSenha@456"));

        final var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userGateway).save(userCaptor.capture());
        assertTrue("new-hash".equals(userCaptor.getValue().getPasswordHash()));

        final var tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenGateway).save(tokenCaptor.capture());
        assertTrue(tokenCaptor.getValue().isUsed());

        verify(refreshTokenGateway).revokeAllByUserId(userId);
    }

    @Test
    void givenWeakNewPassword_whenExecute_thenThrowsNotification() {
        assertThrows(NotificationException.class, () ->
                useCase.execute(ResetPasswordCommand.with("raw", "weak")));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenBlankToken_whenExecute_thenThrowsTokenAlreadyUsed() {
        assertThrows(TokenAlreadyUsedException.class, () ->
                useCase.execute(ResetPasswordCommand.with("  ", "NovaSenha@456")));
    }

    @Test
    void givenUnknownToken_whenExecute_thenThrowsTokenAlreadyUsed() {
        when(resetTokenProvider.hash(any())).thenReturn("thash");
        when(passwordResetTokenGateway.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThrows(TokenAlreadyUsedException.class, () ->
                useCase.execute(ResetPasswordCommand.with("raw", "NovaSenha@456")));
    }

    @Test
    void givenAlreadyUsedToken_whenExecute_thenThrowsTokenAlreadyUsed() {
        final var token = PasswordResetToken.issue(userId, "thash", Instant.now().plusSeconds(3600));
        token.markUsed();
        when(resetTokenProvider.hash(any())).thenReturn("thash");
        when(passwordResetTokenGateway.findByTokenHash(any())).thenReturn(Optional.of(token));

        assertThrows(TokenAlreadyUsedException.class, () ->
                useCase.execute(ResetPasswordCommand.with("raw", "NovaSenha@456")));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenExpiredToken_whenExecute_thenThrowsTokenExpired() {
        final var token = PasswordResetToken.issue(userId, "thash", Instant.now().minusSeconds(10));
        when(resetTokenProvider.hash(any())).thenReturn("thash");
        when(passwordResetTokenGateway.findByTokenHash(any())).thenReturn(Optional.of(token));

        assertThrows(TokenExpiredException.class, () ->
                useCase.execute(ResetPasswordCommand.with("raw", "NovaSenha@456")));
    }

    @Test
    void givenUserNotFound_whenExecute_thenThrowsNotFound() {
        final var token = PasswordResetToken.issue(userId, "thash", Instant.now().plusSeconds(3600));
        when(resetTokenProvider.hash(any())).thenReturn("thash");
        when(passwordResetTokenGateway.findByTokenHash(any())).thenReturn(Optional.of(token));
        when(userGateway.findById(userId)).thenReturn(Optional.empty());
        lenient().when(passwordHasher.hash(any())).thenReturn("x");

        assertThrows(EntityNotFoundException.class, () ->
                useCase.execute(ResetPasswordCommand.with("raw", "NovaSenha@456")));
    }
}
