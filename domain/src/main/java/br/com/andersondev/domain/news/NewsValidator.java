package br.com.andersondev.domain.news;

import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.Validator;

/**
 * Valida invariantes do agregado News (domain-catalog 2.7).
 * title: 5-300 caracteres; summary: 10-500 caracteres; content: minimo 50 caracteres.
 */
public class NewsValidator extends Validator {

    private static final int TITLE_MIN_LENGTH = 5;
    private static final int TITLE_MAX_LENGTH = 300;
    private static final int SUMMARY_MIN_LENGTH = 10;
    private static final int SUMMARY_MAX_LENGTH = 500;
    private static final int CONTENT_MIN_LENGTH = 50;

    private final News news;

    public NewsValidator(final News news, final ValidationHandler handler) {
        super(handler);
        this.news = news;
    }

    @Override
    public void validate() {
        checkTitle();
        checkSummary();
        checkContent();
    }

    private void checkTitle() {
        final var title = this.news.getTitle();
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

    private void checkSummary() {
        final var summary = this.news.getSummary();
        if (summary == null || summary.isBlank()) {
            validationHandler().append(new Error("'summary' e obrigatorio"));
            return;
        }
        final var length = summary.trim().length();
        if (length < SUMMARY_MIN_LENGTH || length > SUMMARY_MAX_LENGTH) {
            validationHandler().append(
                    new Error("'summary' deve ter entre " + SUMMARY_MIN_LENGTH + " e " + SUMMARY_MAX_LENGTH + " caracteres"));
        }
    }

    private void checkContent() {
        final var content = this.news.getContent();
        if (content == null || content.isBlank()) {
            validationHandler().append(new Error("'content' e obrigatorio"));
            return;
        }
        final var length = content.trim().length();
        if (length < CONTENT_MIN_LENGTH) {
            validationHandler().append(
                    new Error("'content' deve ter no minimo " + CONTENT_MIN_LENGTH + " caracteres"));
        }
    }
}
