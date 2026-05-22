package br.com.andersondev.infrastructure.moderation.persistence;

import br.com.andersondev.domain.moderation.Report;
import br.com.andersondev.domain.moderation.ReportId;
import br.com.andersondev.domain.moderation.ReportReason;
import br.com.andersondev.domain.moderation.ReportStatus;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidade JPA do agregado Report. IDs como VARCHAR(36).
 */
@Entity(name = "Report")
@Table(name = "reports")
public class ReportJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "reported_user_id", nullable = false, length = 36)
    private String reportedUserId;

    @Column(name = "reported_by_id", nullable = false, length = 36)
    private String reportedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private ReportReason reason;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReportStatus status;

    @Column(name = "admin_note", length = 2000)
    private String adminNote;

    @Column(name = "resolved_by", length = 36)
    private String resolvedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ReportJpaEntity() {
    }

    public static ReportJpaEntity from(final Report report) {
        final var entity = new ReportJpaEntity();
        entity.id = report.getId().getValue();
        entity.reportedUserId = report.getReportedUserId().getValue();
        entity.reportedById = report.getReportedById().getValue();
        entity.reason = report.getReason();
        entity.description = report.getDescription();
        entity.status = report.getStatus();
        entity.adminNote = report.getAdminNote();
        entity.resolvedBy = report.getResolvedBy() != null ? report.getResolvedBy().getValue() : null;
        entity.resolvedAt = report.getResolvedAt();
        entity.createdAt = report.getCreatedAt();
        return entity;
    }

    public Report toAggregate() {
        return Report.with(
                ReportId.from(this.id),
                UserId.from(this.reportedUserId),
                UserId.from(this.reportedById),
                this.reason,
                this.description,
                this.status,
                this.adminNote,
                this.resolvedBy != null ? UserId.from(this.resolvedBy) : null,
                this.resolvedAt,
                this.createdAt
        );
    }

    public String getId() {
        return this.id;
    }

    public ReportStatus getStatus() {
        return this.status;
    }
}
