package br.com.andersondev.application.social.follow;

import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultUnfollowUserUseCaseTest {

    @Mock
    private FollowGateway followGateway;

    @InjectMocks
    private DefaultUnfollowUserUseCase useCase;

    @Test
    void givenAnyRelation_whenExecute_thenDelegatesDelete() {
        final var follower = UserId.unique();
        final var followed = UserId.unique();

        useCase.execute(FollowUserCommand.with(follower.getValue(), followed.getValue()));

        verify(followGateway).deleteByFollowerIdAndFollowedId(eq(follower), eq(followed));
    }

    @Test
    void givenGatewayFailure_whenExecute_thenPropagates() {
        final var follower = UserId.unique();
        final var followed = UserId.unique();
        doThrow(new RuntimeException("db down"))
                .when(followGateway).deleteByFollowerIdAndFollowedId(eq(follower), eq(followed));

        assertThrows(RuntimeException.class,
                () -> useCase.execute(FollowUserCommand.with(follower.getValue(), followed.getValue())));
    }
}
