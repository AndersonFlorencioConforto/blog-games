package br.com.andersondev.application.shelf.list;

import br.com.andersondev.application.shelf.ShelfListItemOutput;
import br.com.andersondev.domain.game.GameSummary;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.ShelfStatus;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Listagem paginada e publica da estante de um usuario (GET /users/{userId}/shelf).
 * Filtro opcional por status. Cada item e enriquecido com dados resumidos do jogo
 * via GameGateway (id, title, coverImageUrl). Itens cujo jogo nao existe mais
 * (deletado) sao omitidos do conteudo retornado.
 * Normaliza paginacao: page &gt;= 0, size entre 1 e 50 (data-strategy secao 3).
 */
public final class DefaultListShelfUseCase extends ListShelfUseCase {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final ShelfItemGateway shelfItemGateway;
    private final GameGateway gameGateway;

    public DefaultListShelfUseCase(final ShelfItemGateway shelfItemGateway, final GameGateway gameGateway) {
        this.shelfItemGateway = Objects.requireNonNull(shelfItemGateway);
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public Pagination<ShelfListItemOutput> execute(final ListShelfCommand command) {
        final var userId = UserId.from(command.userId());
        final var page = Math.max(command.page(), 0);
        final var size = normalizeSize(command.size());
        final var status = parseStatus(command.status());

        final Pagination<ShelfItem> items = this.shelfItemGateway.findByUserId(userId, status, page, size);

        final var gameIds = items.content().stream()
                .map(item -> item.getGameId().getValue())
                .toList();
        final Map<String, GameSummary> summaries = gameIds.isEmpty()
                ? Map.of()
                : this.gameGateway.findSummariesByIds(gameIds);

        final List<ShelfListItemOutput> content = items.content().stream()
                .map(item -> {
                    final var summary = summaries.get(item.getGameId().getValue());
                    return summary == null ? null : ShelfListItemOutput.of(item, summary);
                })
                .filter(Objects::nonNull)
                .toList();

        return new Pagination<>(items.page(), items.size(), items.totalElements(), content);
    }

    private static int normalizeSize(final int requested) {
        if (requested <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(requested, MAX_SIZE);
    }

    private static ShelfStatus parseStatus(final String value) {
        return value == null || value.isBlank() ? null : ShelfStatus.of(value);
    }
}
