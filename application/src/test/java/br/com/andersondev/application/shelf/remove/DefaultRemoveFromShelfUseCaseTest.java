package br.com.andersondev.application.shelf.remove;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRemoveFromShelfUseCaseTest {

    @Mock
    private ShelfItemGateway shelfItemGateway;

    @InjectMocks
    private DefaultRemoveFromShelfUseCase useCase;

    @Test
    void givenExistingItem_whenExecute_thenDeletes() {
        final var userId = UserId.unique();
        final var gameId = GameId.unique();
        when(shelfItemGateway.existsByUserIdAndGameId(any(UserId.class), any(GameId.class)))
                .thenReturn(true);

        useCase.execute(RemoveFromShelfCommand.with(userId.getValue(), gameId.getValue()));

        verify(shelfItemGateway).deleteByUserIdAndGameId(any(UserId.class), any(GameId.class));
    }

    @Test
    void givenItemNotInShelf_whenExecute_thenThrowsNotFound() {
        when(shelfItemGateway.existsByUserIdAndGameId(any(UserId.class), any(GameId.class)))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(RemoveFromShelfCommand.with(
                        UserId.unique().getValue(), GameId.unique().getValue())));
        verify(shelfItemGateway, never()).deleteByUserIdAndGameId(any(), any());
    }
}
