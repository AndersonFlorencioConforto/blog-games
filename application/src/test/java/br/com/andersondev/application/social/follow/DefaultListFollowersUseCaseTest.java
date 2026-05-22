package br.com.andersondev.application.social.follow;

import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.UserSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultListFollowersUseCaseTest {

    @Mock
    private FollowGateway followGateway;

    @InjectMocks
    private DefaultListFollowersUseCase useCase;

    @Test
    void givenFollowers_whenExecute_thenMapsSummaries() {
        when(followGateway.findFollowers(any(UserId.class), anyInt(), anyInt()))
                .thenReturn(new Pagination<>(0, 20, 1L, List.of(UserSummary.of("u1", "Alice", "https://a"))));

        final var page = useCase.execute(ListSocialCommand.with(UserId.unique().getValue(), 0, 20));

        assertEquals(1, page.content().size());
        assertEquals("Alice", page.content().getFirst().name());
        assertEquals("u1", page.content().getFirst().id());
    }

    @Test
    void givenOversizedPage_whenExecute_thenNormalizesSize() {
        when(followGateway.findFollowers(any(UserId.class), anyInt(), anyInt()))
                .thenReturn(new Pagination<>(0, 50, 0L, List.of()));

        useCase.execute(ListSocialCommand.with(UserId.unique().getValue(), -3, 999));

        final var pageCaptor = ArgumentCaptor.forClass(Integer.class);
        final var sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(followGateway).findFollowers(any(UserId.class), pageCaptor.capture(), sizeCaptor.capture());
        assertEquals(0, pageCaptor.getValue());
        assertEquals(50, sizeCaptor.getValue());
    }

    @Test
    void givenZeroSize_whenExecute_thenDefaultsToTwenty() {
        when(followGateway.findFollowers(any(UserId.class), eq(0), eq(20)))
                .thenReturn(new Pagination<>(0, 20, 0L, List.of()));

        useCase.execute(ListSocialCommand.with(UserId.unique().getValue(), 0, 0));

        verify(followGateway).findFollowers(any(UserId.class), eq(0), eq(20));
    }
}
