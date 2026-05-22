package br.com.andersondev.domain.game;

import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.Validator;

import java.util.List;

/**
 * Valida invariantes da entidade Game (domain-catalog 2.2 / business-validation-matrix G-02..G-06).
 * O range de platformScore e garantido pelo VO {@link PlatformScore}.
 */
public class GameValidator extends Validator {

    private static final int TITLE_MIN_LENGTH = 1;
    private static final int TITLE_MAX_LENGTH = 200;
    private static final int DESCRIPTION_MIN_LENGTH = 1;
    private static final int DESCRIPTION_MAX_LENGTH = 5000;
    private static final int EDITORIAL_REVIEW_MAX_LENGTH = 10000;
    private static final int COVER_IMAGE_URL_MAX_LENGTH = 500;
    private static final int LIST_MAX_ITEMS = 10;
    private static final int LIST_ITEM_MAX_LENGTH = 200;

    private final Game game;

    public GameValidator(final Game game, final ValidationHandler handler) {
        super(handler);
        this.game = game;
    }

    @Override
    public void validate() {
        checkTitle();
        checkDescription();
        checkEditorialReview();
        checkCoverImageUrl();
        checkList("pros", this.game.getPros());
        checkList("cons", this.game.getCons());
        checkCategories();
        checkPlatforms();
    }

    private void checkTitle() {
        final var title = this.game.getTitle();
        if (title == null || title.isBlank()) {
            validationHandler().append(new Error("'title' e obrigatorio"));
            return;
        }
        final var length = title.trim().length();
        if (length < TITLE_MIN_LENGTH || length > TITLE_MAX_LENGTH) {
            validationHandler().append(new Error("'title' deve ter entre 1 e 200 caracteres"));
        }
    }

    private void checkDescription() {
        final var description = this.game.getDescription();
        if (description == null || description.isBlank()) {
            validationHandler().append(new Error("'description' e obrigatorio"));
            return;
        }
        final var length = description.trim().length();
        if (length < DESCRIPTION_MIN_LENGTH || length > DESCRIPTION_MAX_LENGTH) {
            validationHandler().append(new Error("'description' deve ter entre 1 e 5000 caracteres"));
        }
    }

    private void checkEditorialReview() {
        final var editorialReview = this.game.getEditorialReview();
        if (editorialReview != null && editorialReview.length() > EDITORIAL_REVIEW_MAX_LENGTH) {
            validationHandler().append(new Error("'editorialReview' deve ter no maximo 10000 caracteres"));
        }
    }

    private void checkCoverImageUrl() {
        final var coverImageUrl = this.game.getCoverImageUrl();
        if (coverImageUrl != null && coverImageUrl.length() > COVER_IMAGE_URL_MAX_LENGTH) {
            validationHandler().append(new Error("'coverImageUrl' deve ter no maximo 500 caracteres"));
        }
    }

    private void checkList(final String field, final List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        if (values.size() > LIST_MAX_ITEMS) {
            validationHandler().append(new Error("'" + field + "' deve ter no maximo 10 itens"));
        }
        for (final var item : values) {
            if (item != null && item.length() > LIST_ITEM_MAX_LENGTH) {
                validationHandler().append(
                        new Error("cada item de '" + field + "' deve ter no maximo 200 caracteres"));
                break;
            }
        }
    }

    private void checkCategories() {
        final var categories = this.game.getCategories();
        if (categories == null || categories.isEmpty()) {
            validationHandler().append(new Error("ao menos uma categoria e obrigatoria"));
        }
    }

    private void checkPlatforms() {
        final var platforms = this.game.getPlatforms();
        if (platforms == null || platforms.isEmpty()) {
            validationHandler().append(new Error("ao menos uma plataforma e obrigatoria"));
        }
    }
}
