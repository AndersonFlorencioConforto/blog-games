package br.com.andersondev.domain.shelf;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.handler.Notification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShelfItemTest {

    @Test
    void givenValidData_whenNewItem_thenCreatesWithTimestamps() {
        final var userId = UserId.unique();
        final var gameId = GameId.unique();

        final var item = ShelfItem.newItem(userId, gameId, ShelfStatus.JA_TENHO);

        assertEquals(userId, item.getUserId());
        assertEquals(gameId, item.getGameId());
        assertEquals(ShelfStatus.JA_TENHO, item.getStatus());
        assertNotNull(item.getAddedAt());
        assertNotNull(item.getUpdatedAt());

        final var notification = Notification.create();
        item.validate(notification);
        assertFalse(notification.hasError());
    }

    @Test
    void givenItem_whenChangeStatus_thenUpdatesStatus() {
        final var item = ShelfItem.newItem(UserId.unique(), GameId.unique(), ShelfStatus.PRETENDO_PEGAR);
        item.changeStatus(ShelfStatus.FAVORITO);
        assertEquals(ShelfStatus.FAVORITO, item.getStatus());
    }

    @Test
    void givenInvalidStatusString_whenParse_thenThrows() {
        assertThrows(DomainException.class, () -> ShelfStatus.of("INVALIDO"));
        assertThrows(DomainException.class, () -> ShelfStatus.of(" "));
    }

    @Test
    void givenValidStatusStrings_whenParse_thenResolvesEnum() {
        assertEquals(ShelfStatus.JA_TENHO, ShelfStatus.of("ja_tenho"));
        assertEquals(ShelfStatus.FAVORITO, ShelfStatus.of("FAVORITO"));
    }
}
