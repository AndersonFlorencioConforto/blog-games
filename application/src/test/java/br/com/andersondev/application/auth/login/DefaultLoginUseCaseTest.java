package br.com.andersondev.application.auth.login;

import br.com.andersondev.application.auth.port.AccessToken;
import br.com.andersondev.application.auth.port.AccessTokenProvider;
import br.com.andersondev.application.auth.port.RefreshTokenProvider;
import br.com.andersondev.domain.auth.RefreshToken;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.exception.InvalidCredentialsException;
import br.com.andersondev.domain.exception.UserBannedException;
import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.PasswordHasherPort;
import br.com.andersondev.domain.user.port.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultLoginUseCaseTest {

    @Mock
    private UserGateway userGateway;
    @Mock
    private PasswordHasherPort passwordHasher;
    @Mock
    private AccessTokenProvider accessTokenProvider;
    @Mock
    private RefreshTokenProvider refreshTokenProvider;
    @Mock
    private RefreshTokenGateway refreshTokenGateway;

    @InjectMocks
    private DefaultLoginUseCase useCase;

    private User activeUser() {
        return User.with(
                UserId.unique(), "Anderson", Email.of("anderson@email.com"),
                "hash", Role.USER, null, null, null, true, Instant.now(), Instant.now());
    }

    @Test
    void givenValidCredentials_whenExecute_thenReturnsTokens() {
        final var user = activeUser();
        when(userGateway.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordHasher.matches(eq("MinhaSenh@123"), eq("hash"))).thenReturn(true);
        when(accessTokenProvider.generate(any(), any(), any()))
                .thenReturn(new AccessToken("jwt", "jti", Instant.now().plusSeconds(900), 900));
        when(refreshTokenProvider.generate())
                .thenReturn(new RefreshTokenProvider.GeneratedRefreshToken(
                        "raw", "rawhash", Instant.now().plusSeconds(604800)));

        final var output = useCase.execute(
                LoginCommand.with("anderson@email.com", "MinhaSenh@123", "device"));

        assertEquals("jwt", output.accessToken());
        assertEquals("raw", output.refreshToken());
        assertEquals(900, output.expiresIn());
        assertEquals("Bearer", output.tokenType());
        verify(refreshTokenGateway).save(any(RefreshToken.class));
    }

    @Test
    void givenUnknownEmail_whenExecute_thenThrowsInvalidCredentials() {
        when(userGateway.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () ->
                useCase.execute(LoginCommand.with("ghost@email.com", "MinhaSenh@123", "device")));
        verify(refreshTokenGateway, never()).save(any());
    }

    @Test
    void givenWrongPassword_whenExecute_thenThrowsInvalidCredentials() {
        when(userGateway.findByEmail(any(Email.class))).thenReturn(Optional.of(activeUser()));
        when(passwordHasher.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () ->
                useCase.execute(LoginCommand.with("anderson@email.com", "wrong", "device")));
    }

    @Test
    void givenMalformedEmail_whenExecute_thenThrowsInvalidCredentials() {
        assertThrows(InvalidCredentialsException.class, () ->
                useCase.execute(LoginCommand.with("not-an-email", "MinhaSenh@123", "device")));
        verify(userGateway, never()).findByEmail(any());
    }

    @Test
    void givenBannedUser_whenExecute_thenThrowsUserBanned() {
        final var banned = User.with(
                UserId.unique(), "Anderson", Email.of("anderson@email.com"),
                "hash", Role.USER, null, null, Instant.now().plusSeconds(3600),
                true, Instant.now(), Instant.now());
        when(userGateway.findByEmail(any(Email.class))).thenReturn(Optional.of(banned));
        when(passwordHasher.matches(any(), any())).thenReturn(true);

        assertThrows(UserBannedException.class, () ->
                useCase.execute(LoginCommand.with("anderson@email.com", "MinhaSenh@123", "device")));
        verify(refreshTokenGateway, never()).save(any());
    }

    @Test
    void givenRefreshGatewayFailure_whenExecute_thenPropagates() {
        final var user = activeUser();
        when(userGateway.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordHasher.matches(any(), any())).thenReturn(true);
        when(accessTokenProvider.generate(any(), any(), any()))
                .thenReturn(new AccessToken("jwt", "jti", Instant.now().plusSeconds(900), 900));
        when(refreshTokenProvider.generate())
                .thenReturn(new RefreshTokenProvider.GeneratedRefreshToken(
                        "raw", "rawhash", Instant.now().plusSeconds(604800)));
        when(refreshTokenGateway.save(any())).thenThrow(new RuntimeException("db down"));

        assertThrows(RuntimeException.class, () ->
                useCase.execute(LoginCommand.with("anderson@email.com", "MinhaSenh@123", "device")));
    }

    @Test
    void givenSuccess_whenExecute_thenAccessTokenNotNull() {
        final var user = activeUser();
        when(userGateway.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordHasher.matches(any(), any())).thenReturn(true);
        when(accessTokenProvider.generate(any(), any(), any()))
                .thenReturn(new AccessToken("jwt", "jti", Instant.now().plusSeconds(900), 900));
        when(refreshTokenProvider.generate())
                .thenReturn(new RefreshTokenProvider.GeneratedRefreshToken(
                        "raw", "rawhash", Instant.now().plusSeconds(604800)));

        assertNotNull(useCase.execute(LoginCommand.with("anderson@email.com", "x", "d")));
    }
}
