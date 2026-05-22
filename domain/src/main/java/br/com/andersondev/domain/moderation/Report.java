package br.com.andersondev.domain.moderation;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.handler.ThrowsValidationHandler;

import java.time.Instant;

/**
 * Raiz do agregado Report (domain-catalog 2.8).
 * Representa uma denuncia feita por um usuario contra outro.
 */
public class Report extends AggregateRoot<ReportId> {

    private final UserId reportedUserId;
    private final UserId reportedById;
    private final ReportReason reason;
    private final String description;
    private ReportStatus status;
    private String adminNote;
    private UserId resolvedBy;
    private Instant resolvedAt;
    private final Instant createdAt;

    private Report(
            final ReportId id,
            final UserId reportedUserId,
            final UserId reportedById,
            final ReportReason reason,
            final String description,
            final ReportStatus status,
            final String adminNote,
            final UserId resolvedBy,
            final Instant resolvedAt,
            final Instant createdAt
    ) {
        super(id);
        this.reportedUserId = reportedUserId;
        this.reportedById = reportedById;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.adminNote = adminNote;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = resolvedAt;
        this.createdAt = createdAt;
        selfValidate();
    }

    /**
     * Cria uma nova denuncia. status=PENDING, createdAt=now, resolvedBy/resolvedAt=null.
     * Lanca DomainException se reportedById == reportedUserId.
     */
    public static Report newReport(
            final UserId reportedUserId,
            final UserId reportedById,
            final ReportReason reason,
            final String description
    ) {
        if (reportedById != null && reportedUserId != null
                && reportedById.getValue().equals(reportedUserId.getValue())) {
            throw DomainException.with(new Error("Um usuario nao pode denunciar a si mesmo"));
        }
        return new Report(
                ReportId.unique(),
                reportedUserId,
                reportedById,
                reason,
                description,
                ReportStatus.PENDING,
                null,
                null,
                null,
                Instant.now()
        );
    }

    /**
     * Reidrata uma denuncia existente a partir da persistencia.
     */
    public static Report with(
            final ReportId id,
            final UserId reportedUserId,
            final UserId reportedById,
            final ReportReason reason,
            final String description,
            final ReportStatus status,
            final String adminNote,
            final UserId resolvedBy,
            final Instant resolvedAt,
            final Instant createdAt
    ) {
        return new Report(id, reportedUserId, reportedById, reason, description,
                status, adminNote, resolvedBy, resolvedAt, createdAt);
    }

    /**
     * Resolve a denuncia com a decisao dada.
     * Lanca DomainException se ja foi resolvida (status != PENDING).
     */
    public Report resolve(
            final ReportStatus decision,
            final String adminNote,
            final UserId resolvedBy
    ) {
        if (this.status != ReportStatus.PENDING) {
            throw DomainException.with(new Error("Denuncia ja foi resolvida"));
        }
        this.status = decision;
        this.adminNote = adminNote;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = Instant.now();
        return this;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new ReportValidator(this, handler).validate();
    }

    private void selfValidate() {
        validate(new ThrowsValidationHandler());
    }

    public UserId getReportedUserId() {
        return this.reportedUserId;
    }

    public UserId getReportedById() {
        return this.reportedById;
    }

    public ReportReason getReason() {
        return this.reason;
    }

    public String getDescription() {
        return this.description;
    }

    public ReportStatus getStatus() {
        return this.status;
    }

    public String getAdminNote() {
        return this.adminNote;
    }

    public UserId getResolvedBy() {
        return this.resolvedBy;
    }

    public Instant getResolvedAt() {
        return this.resolvedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }
}
