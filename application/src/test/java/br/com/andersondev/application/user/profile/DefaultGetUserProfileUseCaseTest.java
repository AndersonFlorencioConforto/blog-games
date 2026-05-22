package br.com.andersondev.application.user.profile;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.social.port.FollowGateway;
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
class DefaultGetUserProfileUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private FollowGateway followGateway;

    @InjectMocks
    private DefaultGetUserProfileUseCase useCase;

    private static User user(final UserId id) {
        return User.with(
                id, "Anderson", br.com.andersondev.domain.user.Email.of("a@email.com"),
                "hash", br.com.andersondev.domain.user.Role.USER,
                "bio", "https://cdn/a.jpg", null, true, Instant.now(), Instant.now());
    }

    @Test
    void givenExistingUser_whenExecute_thenReturnsProfileWithCounts() {
        final var id = UserId.unique();
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(user(id)));
        when(followGateway.countByFollowedId(any(UserId.class))).thenReturn(42L);
        when(followGateway.countByFollowerId(any(UserId.class))).thenReturn(18L);

        final var output = useCase.execute(id.getValue());

        assertEquals(id.getValue(), output.id());
        assertEquals("Anderson", output.name());
        assertEquals(42L, output.followersCount());
        assertEquals(18L, output.followingCount());
    }

    @Test
    void givenUnknownUser_whenExecute_thenThrowsNotFound() {
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(UserId.unique().getValue()));
    }
}
