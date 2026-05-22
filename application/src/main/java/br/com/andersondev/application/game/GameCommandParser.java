package br.com.andersondev.application.game;

import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.Platform;
import br.com.andersondev.domain.game.PlatformScore;
import br.com.andersondev.domain.validation.handler.Notification;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Converte os campos brutos dos commands de jogo (strings/BigDecimal) para os VOs
 * de dominio, acumulando erros de conversao no {@link Notification}.
 * Compartilhado entre CreateGame e UpdateGame.
 */
public final class GameCommandParser {

    private GameCommandParser() {
    }

    public static PlatformScore parseScore(final Notification notification, final BigDecimal rawScore) {
        return notification.validate(() -> PlatformScore.of(rawScore));
    }

    public static Set<Category> parseCategories(final Notification notification, final List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return EnumSet.noneOf(Category.class);
        }
        final var set = EnumSet.noneOf(Category.class);
        for (final var value : raw) {
            final var category = notification.validate(() -> Category.of(value));
            if (category != null) {
                set.add(category);
            }
        }
        return set;
    }

    public static Set<Platform> parsePlatforms(final Notification notification, final List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return EnumSet.noneOf(Platform.class);
        }
        final var set = EnumSet.noneOf(Platform.class);
        for (final var value : raw) {
            final var platform = notification.validate(() -> Platform.of(value));
            if (platform != null) {
                set.add(platform);
            }
        }
        return set;
    }
}
