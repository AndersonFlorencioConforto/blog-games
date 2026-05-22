package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.Validator;

/**
 * Valida invariantes do agregado Thread (domain-catalog 2.6).
 * title: 5-200 caracteres; content: 10-5000 caracteres.
 */
public class ThreadValidator extends Validator {

    private static final int TITLE_MIN_LENGTH = 5;
    private static final int TITLE_MAX_LENGTH = 200;
    private static final int CONTENT_MIN_LENGTH = 10;
    private static final int CONTENT_MAX_LENGTH = 5000;

    private final Thread thread;

    public ThreadValidator(final Thread thread, final ValidationHandler handler) {
        super(handler);
        this.thread = thread;
    }

    @Override
    public void validate() {
        checkTitle();
        checkContent();
    }

    private void checkTitle() {
        final var title = this.thread.getTitle();
        if (title == null || title.isBlank()) {
            validationHandler().append(new Error("'title' e obrigatorio"));
            return;
        }
        final var length = title.trim().length();
        if (length < TITLE_MIN_LENGTH || length > TITLE_MAX_LENGTH) {
            validationHandler().append(
                    new Error("'title' deve ter entre " + TITLE_MIN_LENGTH + " e " + TITLE_MAX_LENGTH + " caracteres"));
        }
    }

    private void checkContent() {
        final var content = this.thread.getContent();
        if (content == null || content.isBlank()) {
            validationHandler().append(new Error("'content' e obrigatorio"));
            return;
        }
        final var length = content.trim().length();
        if (length < CONTENT_MIN_LENGTH || length > CONTENT_MAX_LENGTH) {
            validationHandler().append(
                    new Error("'content' deve ter entre " + CONTENT_MIN_LENGTH + " e " + CONTENT_MAX_LENGTH + " caracteres"));
        }
    }
}
