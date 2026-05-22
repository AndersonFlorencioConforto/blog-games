package br.com.andersondev.domain.moderation;

import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportTest {

    private static final UserId REPORTED_USER_ID = UserId.unique();
    private static final UserId REPORTED_BY_ID = UserId.unique();

    @Test
    void givenValidParams_whenNewReport_thenCreateReport() {
        // Act
        final var report = Report.newReport(
                REPORTED_USER_ID,
                REPORTED_BY_ID,
                ReportReason.SPAM,
                "Este usuario esta enviando spam."
        );

        // Assert
        assertNotNull(report.getId());
        assertEquals(REPORTED_USER_ID, report.getReportedUserId());
        assertEquals(REPORTED_BY_ID, report.getReportedById());
        assertEquals(ReportReason.SPAM, report.getReason());
        assertEquals("Este usuario esta enviando spam.", report.getDescription());
        assertEquals(ReportStatus.PENDING, report.getStatus());
        assertNull(report.getAdminNote());
        assertNull(report.getResolvedBy());
        assertNull(report.getResolvedAt());
        assertNotNull(report.getCreatedAt());
    }

    @Test
    void givenSameUserIds_whenNewReport_thenThrowDomainException() {
        // Arrange
        final var sameId = UserId.unique();

        // Act + Assert
        final var ex = assertThrows(DomainException.class, () ->
                Report.newReport(sameId, sameId, ReportReason.HARASSMENT, null));

        assertTrue(ex.getMessage().contains("nao pode denunciar a si mesmo"));
    }

    @Test
    void givenPendingReport_whenResolve_thenStatusChanges() {
        // Arrange
        final var report = Report.newReport(
                REPORTED_USER_ID,
                REPORTED_BY_ID,
                ReportReason.ABUSIVE_BEHAVIOR,
                null
        );
        final var adminId = UserId.unique();

        // Act
        report.resolve(ReportStatus.APPROVED, "Comportamento confirmado.", adminId);

        // Assert
        assertEquals(ReportStatus.APPROVED, report.getStatus());
        assertEquals("Comportamento confirmado.", report.getAdminNote());
        assertEquals(adminId, report.getResolvedBy());
        assertNotNull(report.getResolvedAt());
    }

    @Test
    void givenAlreadyResolvedReport_whenResolve_thenThrowDomainException() {
        // Arrange
        final var report = Report.newReport(
                REPORTED_USER_ID,
                REPORTED_BY_ID,
                ReportReason.SPAM,
                null
        );
        final var adminId = UserId.unique();
        report.resolve(ReportStatus.APPROVED, "Primeira resolucao.", adminId);

        // Act + Assert
        final var ex = assertThrows(DomainException.class, () ->
                report.resolve(ReportStatus.REJECTED, "Tentativa de segunda resolucao.", adminId));

        assertTrue(ex.getMessage().contains("ja foi resolvida"));
    }
}
