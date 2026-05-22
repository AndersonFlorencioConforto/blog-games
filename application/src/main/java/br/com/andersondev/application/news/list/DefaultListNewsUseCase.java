package br.com.andersondev.application.news.list;

import br.com.andersondev.application.news.NewsSummaryOutput;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.news.port.NewsGateway;
import br.com.andersondev.domain.shared.Pagination;

import java.util.Objects;

/**
 * Lista noticias paginadas, opcionalmente filtradas por jogo relacionado.
 */
public final class DefaultListNewsUseCase extends ListNewsUseCase {

    private final NewsGateway newsGateway;

    public DefaultListNewsUseCase(final NewsGateway newsGateway) {
        this.newsGateway = Objects.requireNonNull(newsGateway);
    }

    @Override
    public Pagination<NewsSummaryOutput> execute(final ListNewsCommand command) {
        if (command.relatedGameId() != null) {
            final var gameId = GameId.from(command.relatedGameId());
            return this.newsGateway.findByRelatedGameId(gameId, command.page(), command.size())
                    .map(NewsSummaryOutput::from);
        }
        return this.newsGateway.findAll(command.page(), command.size())
                .map(NewsSummaryOutput::from);
    }
}
