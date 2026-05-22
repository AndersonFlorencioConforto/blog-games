package br.com.andersondev.application.auth.password;

import br.com.andersondev.application.auth.port.PasswordResetLinkBuilder;
import br.com.andersondev.application.auth.port.ResetTokenProvider;
import br.com.andersondev.domain.auth.PasswordResetToken;
import br.com.andersondev.domain.auth.port.EmailPort;
import br.com.andersondev.domain.auth.port.PasswordResetTokenGateway;
import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRequestPasswordResetUseCaseTest {

    @Mock
    private UserGateway userGateway;
    @Mock
    private PasswordResetTokenGateway passwordResetTokenGateway;
    @Mock
    private ResetTokenProvider resetTokenProvider;
    @Mock
    private PasswordResetLinkBuilder linkBuilder;
    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private DefaultRequestPasswordResetUseCase useCase;

    private User user() {
        return User.with(UserId.unique(), "Anderson", Email.of("anderson@email.com"),
                "hash", Role.USER, null, null, null, true, Instant.now(), Instant.now());
    }

    @Test
    void givenExistingEmail_whenExecute_thenGeneratesTokenAndSendsEmail() {
        when(userGateway.findByEmail(any(Email.class))).thenReturn(Optional.of(user()));
        when(resetTokenProvider.generate())
                .thenReturn(new ResetTokenProvider.GeneratedResetToken(
                        "raw", "hash", Instant.now().plusSeconds(3600)));
        when(linkBuilder.build(eq("raw"))).thenReturn("http://app/reset?token=raw");

        useCase.execute(RequestPasswordResetCommand.with("anderson@email.com"));

        verify(passwordResetTokenGateway).save(any(PasswordResetToken.class));
        verify(emailPort).sendPasswordResetEmail(eq("anderson@email.com"), eq("http://app/reset?token=raw"));
    }

    @Test
    void givenUnknownEmail_whenExecute_thenDoesNothingButSucceeds() {
        when(userGateway.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertDoesNotThrow(() ->
                useCase.execute(RequestPasswordResetCommand.with("ghost@email.com")));

        verify(passwordResetTokenGateway, never()).save(any());
        verify(emailPort, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void givenMalformedEmail_whenExecute_thenDoesNotLeakAndSucceeds() {
        assertDoesNotThrow(() ->
                useCase.execute(RequestPasswordResetCommand.with("not-an-email")));

        verify(userGateway, never()).findByEmail(any());
        verify(emailPort, never()).sendPasswordResetEmail(any(), any());
    }
}
