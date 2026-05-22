package br.com.andersondev.application.game.list;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameSearchQuery;
import br.com.andersondev.domain.game.Platform;
import br.com.andersondev.domain.game.PlatformScore;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.shared.Pagination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultListGamesUseCaseTest {

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultListGamesUseCase useCase;

    private static Game game() {
        return Game.newGame(
                "Title", "desc", null, PlatformScore.of(new BigDecimal("8.0")),
                List.of(), List.of(), null,
                EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
    }

    @Test
    void givenFilters_whenExecute_thenBuildsQueryAndMapsItems() {
        when(gameGateway.findAll(any(GameSearchQuery.class)))
                .thenReturn(new Pagination<>(0, 20, 1L, List.of(game())));

        final var pagination = useCase.execute(
                ListGamesCommand.with(0, 20, "createdAt", "desc", "RPG", "PC"));

        assertEquals(1, pagination.content().size());
        assertEquals("Title", pagination.content().getFirst().title());

        final var captor = ArgumentCaptor.forClass(GameSearchQuery.class);
        verify(gameGateway).findAll(captor.capture());
        assertEquals(Category.RPG, captor.getValue().category());
        assertEquals(Platform.PC, captor.getValue().platform());
    }

    @Test
    void givenNoFilters_whenExecute_thenNullFiltersAndDefaults() {
        when(gameGateway.findAll(any(GameSearchQuery.class)))
                .thenReturn(new Pagination<>(0, 20, 0L, List.of()));

        useCase.execute(ListGamesCommand.with(-1, 0, null, null, null, null));

        final var captor = ArgumentCaptor.forClass(GameSearchQuery.class);
        verify(gameGateway).findAll(captor.capture());
        assertEquals(0, captor.getValue().page());
        assertEquals(20, captor.getValue().size());
        assertEquals("createdAt", captor.getValue().sort());
        assertEquals("desc", captor.getValue().direction());
        // filtros nulos quando nao informados
        assertEquals(null, captor.getValue().category());
        assertEquals(null, captor.getValue().platform());
    }

    @Test
    void givenSizeAboveMax_whenExecute_thenCapsAtFifty() {
        when(gameGateway.findAll(any(GameSearchQuery.class)))
                .thenReturn(new Pagination<>(0, 50, 0L, List.of()));

        useCase.execute(ListGamesCommand.with(0, 999, null, null, null, null));

        final var captor = ArgumentCaptor.forClass(GameSearchQuery.class);
        verify(gameGateway).findAll(captor.capture());
        assertEquals(50, captor.getValue().size());
    }

    @Test
    void givenInvalidCategoryFilter_whenExecute_thenThrows() {
        assertThrows(DomainException.class,
                () -> useCase.execute(ListGamesCommand.with(0, 20, null, null, "FPS", null)));
    }
}
