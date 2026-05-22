package br.com.andersondev.application.user.register;

import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.port.PasswordHasherPort;
import br.com.andersondev.domain.user.port.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRegisterUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordHasherPort passwordHasher;

    @InjectMocks
    private DefaultRegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        lenient().when(passwordHasher.hash(any())).thenReturn("hashed");
    }

    @Test
    void givenValidCommand_whenExecute_thenRegistersUser() {
        when(userGateway.existsByEmail(any(Email.class))).thenReturn(false);
        when(userGateway.save(any(User.class))).thenAnswer(returnsFirstArg());

        final var command = RegisterUserCommand.with("Anderson", "anderson@email.com", "MinhaSenh@123");

        final var output = useCase.execute(command);

        assertEquals("anderson@email.com", output.email());
        assertEquals("USER", output.role());
        verify(passwordHasher).hash("MinhaSenh@123");
        verify(userGateway).save(any(User.class));
    }

    @Test
    void givenBlankEmail_whenExecute_thenThrowsNotification() {
        final var command = RegisterUserCommand.with("Anderson", "  ", "MinhaSenh@123");

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenInvalidEmailFormat_whenExecute_thenThrowsNotification() {
        final var command = RegisterUserCommand.with("Anderson", "invalid", "MinhaSenh@123");

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenWeakPassword_whenExecute_thenThrowsNotification() {
        final var command = RegisterUserCommand.with("Anderson", "anderson@email.com", "weak");

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenBlankName_whenExecute_thenThrowsNotification() {
        final var command = RegisterUserCommand.with(" ", "anderson@email.com", "MinhaSenh@123");

        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenExistingEmail_whenExecute_thenThrowsDuplicate() {
        when(userGateway.existsByEmail(any(Email.class))).thenReturn(true);

        final var command = RegisterUserCommand.with("Anderson", "anderson@email.com", "MinhaSenh@123");

        assertThrows(DuplicateEntityException.class, () -> useCase.execute(command));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenGatewayFailureOnSave_whenExecute_thenPropagates() {
        when(userGateway.existsByEmail(any(Email.class))).thenReturn(false);
        when(userGateway.save(any(User.class))).thenThrow(new RuntimeException("db down"));

        final var command = RegisterUserCommand.with("Anderson", "anderson@email.com", "MinhaSenh@123");

        assertThrows(RuntimeException.class, () -> useCase.execute(command));
    }
}
