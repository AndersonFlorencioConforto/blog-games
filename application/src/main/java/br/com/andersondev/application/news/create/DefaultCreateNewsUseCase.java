package br.com.andersondev.application.news.create;

import br.com.andersondev.application.news.NewsOutput;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.port.NewsGateway;
import br.com.andersondev.domain.user.UserId;

import java.util.Objects;

/**
 * Cria uma nova noticia.
 * Fluxo:
 *  1. Se relatedGameId fornecido, verifica que o jogo existe e nao esta deletado.
 *  2. Cria o agregado News e persiste.
 */
public final class DefaultCreateNewsUseCase extends CreateNewsUseCase {

    private final NewsGateway newsGateway;
    private final GameGateway gameGateway;

    public DefaultCreateNewsUseCase(final NewsGateway newsGateway, final GameGateway gameGateway) {
        this.newsGateway = Objects.requireNonNull(newsGateway);
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public NewsOutput execute(final CreateNewsCommand command) {
        final var authorId = UserId.from(command.authorId());

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

        final var news = News.newNews(
                command.title(),
                command.summary(),
                command.content(),
                command.coverImageUrl(),
                authorId,
                relatedGameId
        );

        return NewsOutput.from(this.newsGateway.save(news));
    }
}
