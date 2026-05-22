package br.com.andersondev.application.social.follow;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.SelfReferenceException;
import br.com.andersondev.domain.social.Follow;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultFollowUserUseCaseTest {

    @Mock
    private FollowGateway followGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private DefaultFollowUserUseCase useCase;

    private static User user(final UserId id) {
        return User.with(id, "U", Email.of("u@email.com"), "hash", Role.USER,
                null, null, null, true, Instant.now(), Instant.now());
    }

    @Test
    void givenTargetExistsAndNotFollowing_whenExecute_thenSaves() {
        final var follower = UserId.unique();
        final var followed = UserId.unique();
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(user(followed)));
        when(followGateway.existsByFollowerIdAndFollowedId(any(UserId.class), any(UserId.class)))
                .thenReturn(false);
        when(followGateway.save(any(Follow.class))).thenAnswer(returnsFirstArg());

        useCase.execute(FollowUserCommand.with(follower.getValue(), followed.getValue()));

        verify(followGateway).save(any(Follow.class));
    }

    @Test
    void givenAlreadyFollowing_whenExecute_thenIdempotentNoSave() {
        final var follower = UserId.unique();
        final var followed = UserId.unique();
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(user(followed)));
        when(followGateway.existsByFollowerIdAndFollowedId(any(UserId.class), any(UserId.class)))
                .thenReturn(true);

        useCase.execute(FollowUserCommand.with(follower.getValue(), followed.getValue()));

        verify(followGateway, never()).save(any());
    }

    @Test
    void givenFollowingSelf_whenExecute_thenThrowsSelfReference() {
        final var user = UserId.unique().getValue();
        // userGateway pode nao ser chamado: a invariante de auto-follow falha antes.
        lenient().when(userGateway.findById(any(UserId.class))).thenReturn(Optional.empty());

        assertThrows(SelfReferenceException.class,
                () -> useCase.execute(FollowUserCommand.with(user, user)));
        verify(followGateway, never()).save(any());
    }

    @Test
    void givenUnknownTarget_whenExecute_thenThrowsNotFound() {
        final var follower = UserId.unique();
        final var followed = UserId.unique();
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(FollowUserCommand.with(follower.getValue(), followed.getValue())));
        verify(followGateway, never()).save(any());
    }

    @Test
    void givenGatewayFailure_whenExecute_thenPropagates() {
        final var follower = UserId.unique();
        final var followed = UserId.unique();
        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(user(followed)));
        when(followGateway.existsByFollowerIdAndFollowedId(any(UserId.class), any(UserId.class)))
                .thenReturn(false);
        when(followGateway.save(any(Follow.class))).thenThrow(new RuntimeException("db down"));

        assertThrows(RuntimeException.class,
                () -> useCase.execute(FollowUserCommand.with(follower.getValue(), followed.getValue())));
    }
}
