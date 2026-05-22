package br.com.andersondev.application.game.create;

import br.com.andersondev.application.game.GameCommandParser;
import br.com.andersondev.application.game.GameOutput;
import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.validation.handler.Notification;

import java.util.Objects;

/**
 * Cadastro de jogo (G-02..G-08).
 * Fluxo:
 *  1. Converte score/categorias/plataformas para VOs (coletando erros).
 *  2. Constroi e valida o agregado Game.
 *  3. Verifica unicidade de titulo (G-03 -> 409).
 *  4. Persiste.
 */
public final class DefaultCreateGameUseCase extends CreateGameUseCase {

    private final GameGateway gameGateway;

    public DefaultCreateGameUseCase(final GameGateway gameGateway) {
        this.gameGateway = Objects.requireNonNull(gameGateway);
    }

    @Override
    public GameOutput execute(final CreateGameCommand command) {
        final var notification = Notification.create();

        final var platformScore = GameCommandParser.parseScore(notification, command.platformScore());
        final var categories = GameCommandParser.parseCategories(notification, command.categories());
        final var platforms = GameCommandParser.parsePlatforms(notification, command.platforms());

        final var game = Game.newGame(
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
            throw new NotificationException("Nao foi possivel cadastrar o jogo", notification);
        }

        if (this.gameGateway.existsByTitle(command.title())) {
            throw DuplicateEntityException.with("Ja existe um jogo com este titulo");
        }

        return GameOutput.from(this.gameGateway.save(game));
    }
}
