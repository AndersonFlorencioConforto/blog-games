package br.com.andersondev.application.game.update;

import br.com.andersondev.application.game.GameCommandParser;
import br.com.andersondev.application.game.GameOutput;
import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.validation.handler.Notification;

import java.util.Objects;

/**
 * Atualizacao de jogo (G-02..G-08).
 * Fluxo:
 *  1. Resolve o jogo (404 se nao existir ou estiver deletado).
 *  2. Converte e valida os novos atributos.
 *  3. Verifica unicidade de titulo desconsiderando o proprio jogo (409).
 *  4. Aplica a atualizacao e persiste.
 */
public final class DefaultUpdateGameUseCase extends UpdateGameUseCase {

    private final GameGateway gameGateway;

    public DefaultUpdateGameUseCase(final GameGateway gameGateway) {
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public GameOutput execute(final UpdateGameCommand command) {
        final var gameId = GameId.from(command.id());
        final var game = this.gameGateway.findById(gameId)
                .filter(g -> !g.isDeleted())
                .orElseThrow(() -> EntityNotFoundException.with(Game.class, gameId));

        final var notification = Notification.create();

        final var platformScore = GameCommandParser.parseScore(notification, command.platformScore());
        final var categories = GameCommandParser.parseCategories(notification, command.categories());
        final var platforms = GameCommandParser.parsePlatforms(notification, command.platforms());

        game.update(
                command.title(),
                command.description(),
                command.editorialReview(),
                platformScore,
                command.pros(),
                command.cons(),
                command.coverImageUrl(),
                categories,
                platforms
        );
        game.validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("Nao foi possivel atualizar o jogo", notification);
        }

        if (this.gameGateway.existsByTitleAndIdNot(command.title(), gameId)) {
            throw DuplicateEntityException.with("Ja existe um jogo com este titulo");
        }

        return GameOutput.from(this.gameGateway.save(game));
    }
}
