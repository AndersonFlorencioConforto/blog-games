package br.com.andersondev.application.shelf.list;

import br.com.andersondev.application.UseCase;
import br.com.andersondev.application.shelf.ShelfListItemOutput;
import br.com.andersondev.domain.shared.Pagination;

public abstract class ListShelfUseCase extends UseCase<ListShelfCommand, Pagination<ShelfListItemOutput>> {
}
