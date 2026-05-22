package br.com.andersondev.application.game.list;

import br.com.andersondev.application.UseCase;
import br.com.andersondev.application.game.GameListItemOutput;
import br.com.andersondev.domain.shared.Pagination;

public abstract class ListGamesUseCase extends UseCase<ListGamesCommand, Pagination<GameListItemOutput>> {
}
