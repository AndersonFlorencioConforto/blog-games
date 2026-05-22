package br.com.andersondev.application.game.list;

import br.com.andersondev.application.game.GameListItemOutput;
import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.GameSearchQuery;
import br.com.andersondev.domain.game.Platform;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.shared.Pagination;

import java.util.Objects;

/**
 * Listagem paginada de jogos (GET /games), com filtros opcionais por categoria e plataforma.
 * Jogos deletados nao aparecem (G-09, garantido pelo gateway).
 * Normaliza paginacao: page &gt;= 0, size entre 1 e 50 (data-strategy secao 3).
 */
public final class DefaultListGamesUseCase extends ListGamesUseCase {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;
    private static final String DEFAULT_SORT = "createdAt";
    private static final String DEFAULT_DIRECTION = "desc";

    private final GameGateway gameGateway;

    public DefaultListGamesUseCase(final GameGateway gameGateway) {
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public Pagination<GameListItemOutput> execute(final ListGamesCommand command) {
        final var page = Math.max(command.page(), 0);
        final var size = normalizeSize(command.size());
        final var sort = isBlank(command.sort()) ? DEFAULT_SORT : command.sort();
        final var direction = isBlank(command.direction()) ? DEFAULT_DIRECTION : command.direction();
        final var category = parseCategory(command.category());
        final var platform = parsePlatform(command.platform());

        final var query = GameSearchQuery.with(page, size, sort, direction, category, platform);
        return this.gameGateway.findAll(query).map(GameListItemOutput::from);
    }

    private static int normalizeSize(final int requested) {
        if (requested <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(requested, MAX_SIZE);
    }

    private static Category parseCategory(final String value) {
        return isBlank(value) ? null : Category.of(value);
    }

    private static Platform parsePlatform(final String value) {
        return isBlank(value) ? null : Platform.of(value);
    }

    private static boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}
