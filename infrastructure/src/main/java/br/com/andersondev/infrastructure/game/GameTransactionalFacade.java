package br.com.andersondev.infrastructure.game;

import br.com.andersondev.application.game.GameListItemOutput;
import br.com.andersondev.application.game.GameOutput;
import br.com.andersondev.application.game.create.CreateGameCommand;
import br.com.andersondev.application.game.create.CreateGameUseCase;
import br.com.andersondev.application.game.delete.DeleteGameCommand;
import br.com.andersondev.application.game.delete.DeleteGameUseCase;
import br.com.andersondev.application.game.get.GetGameByIdUseCase;
import br.com.andersondev.application.game.list.ListGamesCommand;
import br.com.andersondev.application.game.list.ListGamesUseCase;
import br.com.andersondev.application.game.update.UpdateGameCommand;
import br.com.andersondev.application.game.update.UpdateGameUseCase;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aplica as fronteiras transacionais (transaction-boundaries / Jogos) sobre os casos
 * de uso de catalogo, que sao Java puro na camada application. Reads sao readOnly.
 */
@Service
public class GameTransactionalFacade {

    private final CreateGameUseCase createGameUseCase;
    private final UpdateGameUseCase updateGameUseCase;
    private final DeleteGameUseCase deleteGameUseCase;
    private final GetGameByIdUseCase getGameByIdUseCase;
    private final ListGamesUseCase listGamesUseCase;

    public GameTransactionalFacade(
            final CreateGameUseCase createGameUseCase,
            final UpdateGameUseCase updateGameUseCase,
            final DeleteGameUseCase deleteGameUseCase,
            final GetGameByIdUseCase getGameByIdUseCase,
            final ListGamesUseCase listGamesUseCase
    ) {
        this.createGameUseCase = createGameUseCase;
        this.updateGameUseCase = updateGameUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
        this.getGameByIdUseCase = getGameByIdUseCase;
        this.listGamesUseCase = listGamesUseCase;
    }

    @Transactional
    public GameOutput create(final CreateGameCommand command) {
        return this.createGameUseCase.execute(command);
    }

    @Transactional
    public GameOutput update(final UpdateGameCommand command) {
        return this.updateGameUseCase.execute(command);
    }

    @Transactional
    public void delete(final DeleteGameCommand command) {
        this.deleteGameUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public GameOutput getById(final String id) {
        return this.getGameByIdUseCase.execute(id);
    }

    @Transactional(readOnly = true)
    public Pagination<GameListItemOutput> list(final ListGamesCommand command) {
        return this.listGamesUseCase.execute(command);
    }
}
