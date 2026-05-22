package br.com.andersondev.domain.rating;

import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.Validator;

/**
 * Valida invariantes da entidade Rating. O range de stars e garantido pelo VO {@link Stars};
 * gameId/userId obrigatorios sao garantidos pelos VOs de identidade. Este validador reforca
 * a presenca das referencias para defesa em profundidade.
 */
public class RatingValidator extends Validator {

    private final Rating rating;

    public RatingValidator(final Rating rating, final ValidationHandler handler) {
        super(handler);
        this.rating = rating;
    }

    @Override
    public void validate() {
        if (this.rating.getGameId() == null) {
            validationHandler().append(new Error("'gameId' e obrigatorio"));
        }
        if (this.rating.getUserId() == null) {
            validationHandler().append(new Error("'userId' e obrigatorio"));
        }
        if (this.rating.getStars() == null) {
            validationHandler().append(new Error("'stars' e obrigatorio"));
        }
    }
}
