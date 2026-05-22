package br.com.andersondev.application.social.follow;

import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.UserSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultListFollowingUseCaseTest {

    @Mock
    private FollowGateway followGateway;

    @InjectMocks
    private DefaultListFollowingUseCase useCase;

    @Test
    void givenFollowing_whenExecute_thenMapsSummaries() {
        when(followGateway.findFollowing(any(UserId.class), eq(0), eq(20)))
                .thenReturn(new Pagination<>(0, 20, 1L, List.of(UserSummary.of("u2", "Bob", null))));

        final var page = useCase.execute(ListSocialCommand.with(UserId.unique().getValue(), 0, 20));

        assertEquals(1, page.content().size());
        assertEquals("Bob", page.content().getFirst().name());
        verify(followGateway).findFollowing(any(UserId.class), eq(0), eq(20));
    }
}
