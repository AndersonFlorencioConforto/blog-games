package br.com.andersondev.application.auth.logout;

import br.com.andersondev.application.auth.port.RefreshTokenProvider;
import br.com.andersondev.domain.auth.BlocklistedToken;
import br.com.andersondev.domain.auth.RefreshToken;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.auth.port.TokenBlocklistGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultLogoutUseCaseTest {

    @Mock
    private TokenBlocklistGateway tokenBlocklistGateway;
    @Mock
    private RefreshTokenGateway refreshTokenGateway;
    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @InjectMocks
    private DefaultLogoutUseCase useCase;

    @Test
    void givenJtiAndRefreshToken_whenExecute_thenBlocklistsJtiAndRevokesRefresh() {
        final var userId = UserId.unique();
        final var token = RefreshToken.issue(userId, "hash", Instant.now().plusSeconds(3600), "d");
        when(refreshTokenProvider.hash(eq("raw"))).thenReturn("hash");
        when(refreshTokenGateway.findByTokenHash("hash")).thenReturn(Optional.of(token));

        final var expiresAt = Instant.now().plusSeconds(900);
        useCase.execute(LogoutCommand.with(userId.getValue(), "jti-1", expiresAt, "raw"));

        final var captor = ArgumentCaptor.forClass(BlocklistedToken.class);
        verify(tokenBlocklistGateway).save(captor.capture());
        assertEquals("jti-1", captor.getValue().getJti());

        final var refreshCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenGateway).save(refreshCaptor.capture());
        assertTrue(refreshCaptor.getValue().isRevoked());
    }

    @Test
    void givenNoRefreshToken_whenExecute_thenRevokesAllUserTokens() {
        final var userId = UserId.unique();
        final var expiresAt = Instant.now().plusSeconds(900);

        useCase.execute(LogoutCommand.with(userId.getValue(), "jti-1", expiresAt, null));

        verify(tokenBlocklistGateway).save(any(BlocklistedToken.class));
        verify(refreshTokenGateway).revokeAllByUserId(any(UserId.class));
        verify(refreshTokenGateway, never()).findByTokenHash(any());
    }

    @Test
    void givenUnknownRefreshToken_whenExecute_thenStillBlocklistsJti() {
        when(refreshTokenProvider.hash(any())).thenReturn("hash");
        when(refreshTokenGateway.findByTokenHash(any())).thenReturn(Optional.empty());

        useCase.execute(LogoutCommand.with(
                UserId.unique().getValue(), "jti-1", Instant.now().plusSeconds(900), "raw"));

        verify(tokenBlocklistGateway).save(any(BlocklistedToken.class));
        verify(refreshTokenGateway, never()).save(any());
    }

    @Test
    void givenNullJti_whenExecute_thenSkipsBlocklist() {
        final var userId = UserId.unique();

        useCase.execute(LogoutCommand.with(userId.getValue(), null, null, null));

        verify(tokenBlocklistGateway, never()).save(any());
        verify(refreshTokenGateway).revokeAllByUserId(any(UserId.class));
    }
}
