package br.com.andersondev.infrastructure.shelf;

import br.com.andersondev.application.shelf.ShelfItemOutput;
import br.com.andersondev.application.shelf.ShelfListItemOutput;
import br.com.andersondev.application.shelf.add.AddToShelfCommand;
import br.com.andersondev.application.shelf.add.AddToShelfUseCase;
import br.com.andersondev.application.shelf.list.ListShelfCommand;
import br.com.andersondev.application.shelf.list.ListShelfUseCase;
import br.com.andersondev.application.shelf.remove.RemoveFromShelfCommand;
import br.com.andersondev.application.shelf.remove.RemoveFromShelfUseCase;
import br.com.andersondev.application.shelf.update.UpdateShelfStatusCommand;
import br.com.andersondev.application.shelf.update.UpdateShelfStatusUseCase;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fronteiras transacionais (transaction-boundaries) dos casos de uso de Estante,
 * que sao Java puro na camada application. Leitura publica e readOnly.
 */
@Service
public class ShelfTransactionalFacade {

    private final AddToShelfUseCase addToShelfUseCase;
    private final UpdateShelfStatusUseCase updateShelfStatusUseCase;
    private final RemoveFromShelfUseCase removeFromShelfUseCase;
    private final ListShelfUseCase listShelfUseCase;

    public ShelfTransactionalFacade(
            final AddToShelfUseCase addToShelfUseCase,
            final UpdateShelfStatusUseCase updateShelfStatusUseCase,
            final RemoveFromShelfUseCase removeFromShelfUseCase,
            final ListShelfUseCase listShelfUseCase
    ) {
        this.addToShelfUseCase = addToShelfUseCase;
        this.updateShelfStatusUseCase = updateShelfStatusUseCase;
        this.removeFromShelfUseCase = removeFromShelfUseCase;
        this.listShelfUseCase = listShelfUseCase;
    }

    @Transactional
    public ShelfItemOutput add(final AddToShelfCommand command) {
        return this.addToShelfUseCase.execute(command);
    }

    @Transactional
    public ShelfItemOutput updateStatus(final UpdateShelfStatusCommand command) {
        return this.updateShelfStatusUseCase.execute(command);
    }

    @Transactional
    public void remove(final RemoveFromShelfCommand command) {
        this.removeFromShelfUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public Pagination<ShelfListItemOutput> list(final ListShelfCommand command) {
        return this.listShelfUseCase.execute(command);
    }
}
