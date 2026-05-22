package br.com.andersondev.domain.shelf;

import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.Validator;

/**
 * Valida invariantes do item de estante (status obrigatorio, refs presentes).
 * Unicidade do par (userId, gameId) e verificada no caso de uso/banco (S-02).
 */
public class ShelfItemValidator extends Validator {

    private final ShelfItem shelfItem;

    public ShelfItemValidator(final ShelfItem shelfItem, final ValidationHandler handler) {
        super(handler);
        this.shelfItem = shelfItem;
    }

    @Override
    public void validate() {
        if (this.shelfItem.getStatus() == null) {
            validationHandler().append(new Error("'status' e obrigatorio"));
        }
        if (this.shelfItem.getUserId() == null) {
            validationHandler().append(new Error("'userId' e obrigatorio"));
        }
        if (this.shelfItem.getGameId() == null) {
            validationHandler().append(new Error("'gameId' e obrigatorio"));
        }
    }
}
