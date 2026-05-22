package br.com.andersondev.application.user.profile;

import br.com.andersondev.domain.exception.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultGetMyProfileUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private FollowGateway followGateway;

    @InjectMocks
    private DefaultGetMyProfileUseCase useCase;

    @Test
    void givenAuthenticatedUser_whenExecute_thenReturnsEmailAndRole() {
        final var id = UserId.unique();
        final var user = User.with(
                id, "Anderson", Email.of("a@email.com"), "hash", Role.ADMIN,
                "bio", null, null, true, Instant.now(), Instant.now());
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(user));
        when(followGateway.countByFollowedId(any(UserId.class))).thenReturn(1L);
        when(followGateway.countByFollowerId(any(UserId.class))).thenReturn(2L);

        final var output = useCase.execute(id.getValue());

        assertEquals("a@email.com", output.email());
        assertEquals("ADMIN", output.role());
        assertEquals(1L, output.followersCount());
        assertEquals(2L, output.followingCount());
    }

    @Test
    void givenMissingUser_whenExecute_thenThrowsNotFound() {
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(UserId.unique().getValue()));
    }
}
