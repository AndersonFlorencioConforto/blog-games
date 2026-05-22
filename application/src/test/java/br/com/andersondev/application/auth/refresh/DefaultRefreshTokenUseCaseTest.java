package br.com.andersondev.application.auth.refresh;

import br.com.andersondev.application.auth.port.AccessToken;
import br.com.andersondev.application.auth.port.AccessTokenProvider;
import br.com.andersondev.application.auth.port.RefreshTokenProvider;
import br.com.andersondev.domain.auth.RefreshToken;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.TokenExpiredException;
import br.com.andersondev.domain.exception.UserBannedException;
import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRefreshTokenUseCaseTest {

    @Mock
    private RefreshTokenGateway refreshTokenGateway;
    @Mock
    private UserGateway userGateway;
    @Mock
    private AccessTokenProvider accessTokenProvider;
    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @InjectMocks
    private DefaultRefreshTokenUseCase useCase;

    private final UserId userId = UserId.unique();

    private User activeUser() {
        return User.with(userId, "Anderson", Email.of("anderson@email.com"),
                "hash", Role.USER, null, null, null, true, Instant.now(), Instant.now());
    }

    @Test
    void givenValidRefreshToken_whenExecute_thenRotatesAndReturnsNewPair() {
        final var current = RefreshToken.issue(userId, "hash", Instant.now().plusSeconds(3600), "d");
        when(refreshTokenProvider.hash(eq("raw-token"))).thenReturn("hash");
        when(refreshTokenGateway.findByTokenHash("hash")).thenReturn(Optional.of(current));
        when(userGateway.findById(userId)).thenReturn(Optional.of(activeUser()));
        when(accessTokenProvider.generate(any(), any(), any()))
                .thenReturn(new AccessToken("new-jwt", "jti", Instant.now().plusSeconds(900), 900));
        when(refreshTokenProvider.generate())
                .thenReturn(new RefreshTokenProvider.GeneratedRefreshToken(
                        "new-raw", "new-hash", Instant.now().plusSeconds(604800)));

        final var output = useCase.execute(RefreshTokenCommand.with("raw-token", "d"));

        assertEquals("new-jwt", output.accessToken());
        assertEquals("new-raw", output.refreshToken());

        final var captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenGateway, atLeastOnce()).save(captor.capture());
        // o token atual deve ter sido revogado (rotacao)
        assertTrue(captor.getAllValues().stream().anyMatch(RefreshToken::isRevoked));
    }

    @Test
    void givenBlankToken_whenExecute_thenThrowsTokenExpired() {
        assertThrows(TokenExpiredException.class, () ->
                useCase.execute(RefreshTokenCommand.with("  ", "d")));
        verify(refreshTokenGateway, never()).findByTokenHash(any());
    }

    @Test
    void givenUnknownToken_whenExecute_thenThrowsTokenExpired() {
        when(refreshTokenProvider.hash(any())).thenReturn("hash");
        when(refreshTokenGateway.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThrows(TokenExpiredException.class, () ->
                useCase.execute(RefreshTokenCommand.with("raw", "d")));
    }

    @Test
    void givenExpiredToken_whenExecute_thenThrowsTokenExpired() {
        final var expired = RefreshToken.issue(userId, "hash", Instant.now().minusSeconds(10), "d");
        when(refreshTokenProvider.hash(any())).thenReturn("hash");
        when(refreshTokenGateway.findByTokenHash(any())).thenReturn(Optional.of(expired));

        assertThrows(TokenExpiredException.class, () ->
                useCase.execute(RefreshTokenCommand.with("raw", "d")));
    }

    @Test
    void givenRevokedToken_whenExecute_thenThrowsTokenExpired() {
        final var revoked = RefreshToken.issue(userId, "hash", Instant.now().plusSeconds(3600), "d");
        revoked.revoke();
        when(refreshTokenProvider.hash(any())).thenReturn("hash");
        when(refreshTokenGateway.findByTokenHash(any())).thenReturn(Optional.of(revoked));

        assertThrows(TokenExpiredException.class, () ->
                useCase.execute(RefreshTokenCommand.with("raw", "d")));
    }

    @Test
    void givenUserNotFound_whenExecute_thenThrowsNotFound() {
        final var current = RefreshToken.issue(userId, "hash", Instant.now().plusSeconds(3600), "d");
        when(refreshTokenProvider.hash(any())).thenReturn("hash");
        when(refreshTokenGateway.findByTokenHash(any())).thenReturn(Optional.of(current));
        when(userGateway.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                useCase.execute(RefreshTokenCommand.with("raw", "d")));
    }

    @Test
    void givenBannedUser_whenExecute_thenThrowsUserBanned() {
        final var current = RefreshToken.issue(userId, "hash", Instant.now().plusSeconds(3600), "d");
        final var banned = User.with(userId, "Anderson", Email.of("anderson@email.com"),
                "hash", Role.USER, null, null, Instant.now().plusSeconds(3600),
                true, Instant.now(), Instant.now());
        when(refreshTokenProvider.hash(any())).thenReturn("hash");
        when(refreshTokenGateway.findByTokenHash(any())).thenReturn(Optional.of(current));
        when(userGateway.findById(userId)).thenReturn(Optional.of(banned));
        lenient().when(accessTokenProvider.generate(any(), any(), any())).thenReturn(null);

        assertThrows(UserBannedException.class, () ->
                useCase.execute(RefreshTokenCommand.with("raw", "d")));
    }
}
