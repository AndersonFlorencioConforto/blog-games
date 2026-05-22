package br.com.andersondev.application.user.profile;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.social.port.FollowGateway;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUpdateProfileUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private FollowGateway followGateway;

    @InjectMocks
    private DefaultUpdateProfileUseCase useCase;

    private static User user(final UserId id) {
        return User.with(
                id, "Anderson", Email.of("a@email.com"), "hash", Role.USER,
                null, null, null, true, Instant.now(), Instant.now());
    }

    @Test
    void givenValidData_whenExecute_thenUpdatesAndPersists() {
        final var id = UserId.unique();
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(user(id)));
        when(userGateway.save(any(User.class))).thenAnswer(returnsFirstArg());
        when(followGateway.countByFollowedId(any(UserId.class))).thenReturn(0L);
        when(followGateway.countByFollowerId(any(UserId.class))).thenReturn(0L);

        final var output = useCase.execute(UpdateProfileCommand.with(
                id.getValue(), "Novo Nome", "nova bio", "https://cdn.example.com/a.jpg"));

        assertEquals("Novo Nome", output.name());
        assertEquals("nova bio", output.bio());
        verify(userGateway).save(any(User.class));
    }

    @Test
    void givenInvalidName_whenExecute_thenThrowsNotificationAndDoesNotSave() {
        final var id = UserId.unique();
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(user(id)));

        assertThrows(NotificationException.class,
                () -> useCase.execute(UpdateProfileCommand.with(id.getValue(), "a", null, null)));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenInvalidAvatarUrl_whenExecute_thenThrowsNotification() {
        final var id = UserId.unique();
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(user(id)));

        assertThrows(NotificationException.class,
                () -> useCase.execute(UpdateProfileCommand.with(
                        id.getValue(), "Nome Valido", null, "ftp:::invalid")));
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenMissingUser_whenExecute_thenThrowsNotFound() {
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(UpdateProfileCommand.with(
                        UserId.unique().getValue(), "Nome", null, null)));
        verify(userGateway, never()).save(any());
    }
}
