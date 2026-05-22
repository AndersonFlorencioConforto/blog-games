package br.com.andersondev.application.shelf.update;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.ShelfStatus;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUpdateShelfStatusUseCaseTest {

    @Mock
    private ShelfItemGateway shelfItemGateway;

    @InjectMocks
    private DefaultUpdateShelfStatusUseCase useCase;

    @Test
    void givenExistingItem_whenExecute_thenChangesStatus() {
        final var userId = UserId.unique();
        final var gameId = GameId.unique();
        final var item = ShelfItem.newItem(userId, gameId, ShelfStatus.JA_TENHO);
        when(shelfItemGateway.findByUserIdAndGameId(any(UserId.class), any(GameId.class)))
                .thenReturn(Optional.of(item));
        when(shelfItemGateway.save(any(ShelfItem.class))).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(UpdateShelfStatusCommand.with(
                userId.getValue(), gameId.getValue(), "FAVORITO"));

        assertEquals("FAVORITO", output.status());
        verify(shelfItemGateway).save(any(ShelfItem.class));
    }

    @Test
    void givenInvalidStatus_whenExecute_thenThrowsNotification() {
        assertThrows(NotificationException.class,
                () -> useCase.execute(UpdateShelfStatusCommand.with(
                        UserId.unique().getValue(), GameId.unique().getValue(), "INVALIDO")));
        verify(shelfItemGateway, never()).save(any());
    }

    @Test
    void givenItemNotInShelf_whenExecute_thenThrowsNotFound() {
        when(shelfItemGateway.findByUserIdAndGameId(any(UserId.class), any(GameId.class)))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(UpdateShelfStatusCommand.with(
                        UserId.unique().getValue(), GameId.unique().getValue(), "FAVORITO")));
        verify(shelfItemGateway, never()).save(any());
    }
}
