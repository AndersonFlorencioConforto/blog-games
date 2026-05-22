package br.com.andersondev.domain.discussion;

import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.Validator;

/**
 * Valida invariantes do agregado Reply (domain-catalog 2.6).
 * content: 1-2000 caracteres.
 */
public class ReplyValidator extends Validator {

    private static final int CONTENT_MIN_LENGTH = 1;
    private static final int CONTENT_MAX_LENGTH = 2000;

    private final Reply reply;

    public ReplyValidator(final Reply reply, final ValidationHandler handler) {
        super(handler);
        this.reply = reply;
    }

    @Override
    public void validate() {
        checkContent();
    }

    private void checkContent() {
        final var content = this.reply.getContent();
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
