package br.com.andersondev.domain.game;

import br.com.andersondev.domain.game.event.GameCreatedEvent;
import br.com.andersondev.domain.game.event.GameDeletedEvent;
import br.com.andersondev.domain.game.event.GameUpdatedEvent;
import br.com.andersondev.domain.validation.handler.Notification;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    private static Game validGame() {
        return Game.newGame(
                "The Witcher 3",
                "RPG de mundo aberto",
                "Review editorial",
                PlatformScore.of(new BigDecimal("9.5")),
                List.of("Historia incrivel"),
                List.of("Lento no inicio"),
                "https://cdn/cover.jpg",
                EnumSet.of(Category.RPG, Category.ACAO),
                EnumSet.of(Platform.PC, Platform.PLAYSTATION)
        );
    }

    @Test
    void givenValidData_whenNewGame_thenCreatesWithZeroedAggregateAndEvent() {
        final var game = validGame();

        assertNotNull(game.getId());
        assertEquals("The Witcher 3", game.getTitle());
        assertEquals(0, game.getTotalRatings());
        assertEquals(new BigDecimal("0.00"), game.getAverageUserRating());
        assertFalse(game.isDeleted());
        assertEquals(1, game.getDomainEvents().size());
        assertInstanceOf(GameCreatedEvent.class, game.getDomainEvents().getFirst());
    }

    @Test
    void givenValidGame_whenValidate_thenNoErrors() {
        final var notification = Notification.create();

        validGame().validate(notification);

        assertFalse(notification.hasError());
    }

    @Test
    void givenBlankTitle_whenValidate_thenHasError() {
        final var game = Game.newGame(
                " ", "desc", null, PlatformScore.of(BigDecimal.ONE),
                List.of(), List.of(), null,
                EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
        final var notification = Notification.create();

        game.validate(notification);

        assertTrue(notification.hasError());
    }

    @Test
    void givenNoCategory_whenValidate_thenHasError() {
        final var game = Game.newGame(
                "Title", "desc", null, PlatformScore.of(BigDecimal.ONE),
                List.of(), List.of(), null,
                Set.of(), EnumSet.of(Platform.PC));
        final var notification = Notification.create();

        game.validate(notification);

        assertTrue(notification.hasError());
    }

    @Test
    void givenNoPlatform_whenValidate_thenHasError() {
        final var game = Game.newGame(
                "Title", "desc", null, PlatformScore.of(BigDecimal.ONE),
                List.of(), List.of(), null,
                EnumSet.of(Category.RPG), Set.of());
        final var notification = Notification.create();

        game.validate(notification);

        assertTrue(notification.hasError());
    }

    @Test
    void givenMoreThanTenPros_whenValidate_thenHasError() {
        final var pros = java.util.stream.IntStream.range(0, 11).mapToObj(i -> "pro" + i).toList();
        final var game = Game.newGame(
                "Title", "desc", null, PlatformScore.of(BigDecimal.ONE),
                pros, List.of(), null,
                EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
        final var notification = Notification.create();

        game.validate(notification);

        assertTrue(notification.hasError());
    }

    @Test
    void givenProItemTooLong_whenValidate_thenHasError() {
        final var game = Game.newGame(
                "Title", "desc", null, PlatformScore.of(BigDecimal.ONE),
                List.of("a".repeat(201)), List.of(), null,
                EnumSet.of(Category.RPG), EnumSet.of(Platform.PC));
        final var notification = Notification.create();

        game.validate(notification);

        assertTrue(notification.hasError());
    }

    @Test
    void givenGame_whenDelete_thenMarksDeletedAndRegistersEvent() {
        final var game = validGame();
        game.clearEvents();

        game.delete();

        assertTrue(game.isDeleted());
        assertNotNull(game.getDeletedAt());
        assertInstanceOf(GameDeletedEvent.class, game.getDomainEvents().getFirst());
    }

    @Test
    void givenAlreadyDeletedGame_whenDelete_thenIsIdempotent() {
        final var game = validGame();
        game.delete();
        final var firstDeletedAt = game.getDeletedAt();
        game.clearEvents();

        game.delete();

        assertEquals(firstDeletedAt, game.getDeletedAt());
        assertTrue(game.getDomainEvents().isEmpty());
    }

    @Test
    void givenGame_whenApplyRatingAggregate_thenUpdatesAverageAndTotal() {
        final var game = validGame();

        game.applyRatingAggregate(new BigDecimal("4.666"), 3);

        assertEquals(new BigDecimal("4.67"), game.getAverageUserRating());
        assertEquals(3, game.getTotalRatings());
    }

    @Test
    void givenGame_whenApplyRatingAggregateWithNull_thenZeroAverage() {
        final var game = validGame();

        game.applyRatingAggregate(null, 0);

        assertEquals(new BigDecimal("0.00"), game.getAverageUserRating());
        assertEquals(0, game.getTotalRatings());
    }

    @Test
    void givenGame_whenUpdate_thenChangesFieldsAndRegistersEvent() {
        final var game = validGame();
        game.clearEvents();

        game.update(
                "New Title", "new desc", "new review",
                PlatformScore.of(new BigDecimal("8.0")),
                List.of("pro"), List.of("con"), "https://cover",
                EnumSet.of(Category.AVENTURA), EnumSet.of(Platform.XBOX));

        assertEquals("New Title", game.getTitle());
        assertEquals(EnumSet.of(Category.AVENTURA), game.getCategories());
        assertEquals(EnumSet.of(Platform.XBOX), game.getPlatforms());
        assertInstanceOf(GameUpdatedEvent.class, game.getDomainEvents().getFirst());
    }
}
