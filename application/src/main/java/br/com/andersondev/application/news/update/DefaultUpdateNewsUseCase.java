package br.com.andersondev.application.news.update;

import br.com.andersondev.application.news.NewsOutput;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.NewsId;
import br.com.andersondev.domain.news.port.NewsGateway;

import java.util.Objects;

/**
 * Atualiza uma noticia existente.
 * Fluxo:
 *  1. Carrega a noticia ou lanca NotFoundException.
 *  2. Se novo relatedGameId fornecido, verifica que o jogo existe e nao esta deletado.
 *  3. Chama news.update(...) e persiste.
 */
public final class DefaultUpdateNewsUseCase extends UpdateNewsUseCase {

    private final NewsGateway newsGateway;
    private final GameGateway gameGateway;

    public DefaultUpdateNewsUseCase(final NewsGateway newsGateway, final GameGateway gameGateway) {
        this.newsGateway = Objects.requireNonNull(newsGateway);
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public NewsOutput execute(final UpdateNewsCommand command) {
        final var newsId = NewsId.from(command.newsId());
        final var news = this.newsGateway.findById(newsId)
                .orElseThrow(() -> EntityNotFoundException.with(News.class, newsId));

        GameId relatedGameId = null;
        if (command.relatedGameId() != null) {
            relatedGameId = GameId.from(command.relatedGameId());
            final var gameExists = this.gameGateway.findById(relatedGameId)
                    .filter(game -> !game.isDeleted())
                    .isPresent();
            if (!gameExists) {
                throw EntityNotFoundException.with(Game.class, relatedGameId);
            }
        }

        news.update(
                command.title(),
                command.summary(),
                command.content(),
                command.coverImageUrl(),
                relatedGameId
        );

        return NewsOutput.from(this.newsGateway.save(news));
    }
}
