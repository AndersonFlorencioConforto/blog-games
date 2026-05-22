package br.com.andersondev.domain.moderation;

import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.Validator;

/**
 * Valida invariantes do agregado Report (domain-catalog 2.8).
 */
public class ReportValidator extends Validator {

    private static final int DESCRIPTION_MAX_LENGTH = 1000;

    private final Report report;

    public ReportValidator(final Report report, final ValidationHandler handler) {
        super(handler);
        this.report = report;
    }

    @Override
    public void validate() {
        checkReportedUserId();
        checkReportedById();
        checkReason();
        checkDescription();
    }

    private void checkReportedUserId() {
        if (this.report.getReportedUserId() == null) {
            validationHandler().append(new Error("'reportedUserId' e obrigatorio"));
        }
    }

    private void checkReportedById() {
        if (this.report.getReportedById() == null) {
            validationHandler().append(new Error("'reportedById' e obrigatorio"));
        }
    }

    private void checkReason() {
        if (this.report.getReason() == null) {
            validationHandler().append(new Error("'reason' e obrigatorio"));
        }
    }

    private void checkDescription() {
        final var description = this.report.getDescription();
        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH) {
            validationHandler().append(new Error(
                    "'description' deve ter no maximo " + DESCRIPTION_MAX_LENGTH + " caracteres"));
        }
    }
}
