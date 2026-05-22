package br.com.andersondev.application.shelf.add;

import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.Platform;
import br.com.andersondev.domain.game.PlatformScore;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultAddToShelfUseCaseTest {

    @Mock
    private ShelfItemGateway shelfItemGateway;

    @Mock
    private GameGateway gameGateway;

    @InjectMocks
    private DefaultAddToShelfUseCase useCase;

    private static Game game() {
        return Game.newGame("Title", "desc", null, PlatformScore.of(new BigDecimal("8.0")),
                List.of(), List.of(), null, EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
    }

    @Test
    void givenValidNewItem_whenExecute_thenSaves() {
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game()));
        when(shelfItemGateway.existsByUserIdAndGameId(any(UserId.class), any(GameId.class)))
                .thenReturn(false);
        when(shelfItemGateway.save(any(ShelfItem.class))).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(AddToShelfCommand.with(
                UserId.unique().getValue(), GameId.unique().getValue(), "JA_TENHO"));

        assertEquals("JA_TENHO", output.status());
        verify(shelfItemGateway).save(any(ShelfItem.class));
    }

    @Test
    void givenInvalidStatus_whenExecute_thenThrowsNotification() {
        assertThrows(NotificationException.class,
                () -> useCase.execute(AddToShelfCommand.with(
                        UserId.unique().getValue(), GameId.unique().getValue(), "INVALIDO")));
        verify(shelfItemGateway, never()).save(any());
    }

    @Test
    void givenUnknownGame_whenExecute_thenThrowsNotFound() {
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(AddToShelfCommand.with(
                        UserId.unique().getValue(), GameId.unique().getValue(), "FAVORITO")));
        verify(shelfItemGateway, never()).save(any());
    }

    @Test
    void givenDeletedGame_whenExecute_thenThrowsNotFound() {
        final var game = game();
        game.delete();
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(AddToShelfCommand.with(
                        UserId.unique().getValue(), GameId.unique().getValue(), "FAVORITO")));
        verify(shelfItemGateway, never()).save(any());
    }

    @Test
    void givenAlreadyInShelf_whenExecute_thenThrowsDuplicate() {
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game()));
        when(shelfItemGateway.existsByUserIdAndGameId(any(UserId.class), any(GameId.class)))
                .thenReturn(true);

        assertThrows(DuplicateEntityException.class,
                () -> useCase.execute(AddToShelfCommand.with(
                        UserId.unique().getValue(), GameId.unique().getValue(), "JA_TENHO")));
        verify(shelfItemGateway, never()).save(any());
    }

    @Test
    void givenGatewayFailure_whenExecute_thenPropagates() {
        when(gameGateway.findById(any(GameId.class))).thenReturn(Optional.of(game()));
        when(shelfItemGateway.existsByUserIdAndGameId(any(UserId.class), any(GameId.class)))
                .thenReturn(false);
        when(shelfItemGateway.save(any(ShelfItem.class))).thenThrow(new RuntimeException("db down"));

        assertThrows(RuntimeException.class,
                () -> useCase.execute(AddToShelfCommand.with(
                        UserId.unique().getValue(), GameId.unique().getValue(), "JA_TENHO")));
    }
}
