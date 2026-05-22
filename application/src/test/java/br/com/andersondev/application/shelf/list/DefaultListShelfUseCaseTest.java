package br.com.andersondev.application.shelf.list;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.GameSummary;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.ShelfStatus;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultListShelfUseCaseTest {

    @Mock
    private ShelfItemGateway shelfItemGateway;

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultListShelfUseCase useCase;

    @Test
    void givenItems_whenExecute_thenEnrichesWithGameSummary() {
        final var userId = UserId.unique();
        final var gameId = GameId.unique();
        final var item = ShelfItem.newItem(userId, gameId, ShelfStatus.JA_TENHO);
        when(shelfItemGateway.findByUserId(any(UserId.class), isNull(), eq(0), eq(20)))
                .thenReturn(new Pagination<>(0, 20, 1L, List.of(item)));
        when(gameGateway.findSummariesByIds(anyList()))
                .thenReturn(Map.of(gameId.getValue(), GameSummary.of(gameId.getValue(), "Witcher", "https://cover")));

        final var page = useCase.execute(ListShelfCommand.with(userId.getValue(), null, 0, 20));

        assertEquals(1, page.content().size());
        assertEquals("Witcher", page.content().getFirst().game().title());
        assertEquals("JA_TENHO", page.content().getFirst().status());
    }

    @Test
    void givenStatusFilter_whenExecute_thenPassesStatusToGateway() {
        final var userId = UserId.unique();
        when(shelfItemGateway.findByUserId(any(UserId.class), eq(ShelfStatus.FAVORITO), anyInt(), anyInt()))
                .thenReturn(new Pagination<>(0, 20, 0L, List.of()));

        useCase.execute(ListShelfCommand.with(userId.getValue(), "FAVORITO", 0, 20));

        verify(shelfItemGateway).findByUserId(any(UserId.class), eq(ShelfStatus.FAVORITO), eq(0), eq(20));
    }

    @Test
    void givenItemWhoseGameIsMissing_whenExecute_thenOmitsItem() {
        final var userId = UserId.unique();
        final var item = ShelfItem.newItem(userId, GameId.unique(), ShelfStatus.JA_TENHO);
        when(shelfItemGateway.findByUserId(any(UserId.class), isNull(), eq(0), eq(20)))
                .thenReturn(new Pagination<>(0, 20, 1L, List.of(item)));
        when(gameGateway.findSummariesByIds(anyList())).thenReturn(Map.of());

        final var page = useCase.execute(ListShelfCommand.with(userId.getValue(), null, 0, 20));

        assertEquals(0, page.content().size());
        // metadados de pagina sao preservados (totalElements vem do shelf gateway)
        assertEquals(1L, page.totalElements());
    }

    @Test
    void givenOversizedPage_whenExecute_thenNormalizes() {
        final var userId = UserId.unique();
        when(shelfItemGateway.findByUserId(any(UserId.class), isNull(), anyInt(), anyInt()))
                .thenReturn(new Pagination<>(0, 50, 0L, List.of()));

        useCase.execute(ListShelfCommand.with(userId.getValue(), null, -1, 999));

        final var pageCaptor = ArgumentCaptor.forClass(Integer.class);
        final var sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(shelfItemGateway).findByUserId(
                any(UserId.class), isNull(), pageCaptor.capture(), sizeCaptor.capture());
        assertEquals(0, pageCaptor.getValue());
        assertEquals(50, sizeCaptor.getValue());
    }
}
