package br.com.andersondev.application.moderation.resolve;

import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.moderation.Report;
import br.com.andersondev.domain.moderation.ReportReason;
import br.com.andersondev.domain.moderation.ReportStatus;
import br.com.andersondev.domain.moderation.port.ReportGateway;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultResolveReportUseCaseTest {

    @Mock
    private ReportGateway reportGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private DefaultResolveReportUseCase useCase;

    @Test
    void givenRejectDecision_whenExecute_thenReportRejected() {
        // Arrange
        final var reportedUserId = UserId.unique();
        final var reportedById = UserId.unique();
        final var adminId = UserId.unique().getValue();
        final var report = Report.newReport(reportedUserId, reportedById, ReportReason.SPAM, null);

        when(reportGateway.findById(any())).thenReturn(Optional.of(report));
        when(reportGateway.save(any(Report.class))).thenAnswer(returnsFirstArg());

        final var command = ResolveReportCommand.with(
                report.getId().getValue(), "REJECTED", "Denuncia improcedente.", null, adminId);

        // Act
        final var output = useCase.execute(command);

        // Assert
        assertNotNull(output);
        assertEquals("REJECTED", output.status());
        assertEquals("Denuncia improcedente.", output.adminNote());
        assertNotNull(output.resolvedAt());
        verify(reportGateway).save(any(Report.class));
        verify(userGateway, never()).findById(any());
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenApproveWithBan_whenExecute_thenReportApprovedAndUserBanned() {
        // Arrange
        final var reportedUserId = UserId.unique();
        final var reportedById = UserId.unique();
        final var adminId = UserId.unique().getValue();
        final var report = Report.newReport(reportedUserId, reportedById, ReportReason.ABUSIVE_BEHAVIOR, null);
        final var user = User.newUser("Usuario Banido", "banido@email.com", "hash123");

        when(reportGateway.findById(any())).thenReturn(Optional.of(report));
        when(reportGateway.save(any(Report.class))).thenAnswer(returnsFirstArg());
        when(userGateway.findById(reportedUserId)).thenReturn(Optional.of(user));
        when(userGateway.save(any(User.class))).thenAnswer(returnsFirstArg());

        final var command = ResolveReportCommand.with(
                report.getId().getValue(), "APPROVED", "Comportamento abusivo confirmado.", 7, adminId);

        // Act
        final var output = useCase.execute(command);

        // Assert
        assertNotNull(output);
        assertEquals("APPROVED", output.status());
        verify(reportGateway).save(any(Report.class));
        verify(userGateway, times(1)).findById(reportedUserId);
        verify(userGateway, times(1)).save(any(User.class));
    }

    @Test
    void givenNonExistentReport_whenExecute_thenThrowNotFoundException() {
        // Arrange
        final var command = ResolveReportCommand.with(
                "non-existent-id", "APPROVED", null, null, UserId.unique().getValue());

        when(reportGateway.findById(any())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(reportGateway, never()).save(any());
        verify(userGateway, never()).save(any());
    }

    @Test
    void givenApproveWithoutBan_whenExecute_thenReportApprovedAndUserNotBanned() {
        // Arrange
        final var reportedUserId = UserId.unique();
        final var reportedById = UserId.unique();
        final var adminId = UserId.unique().getValue();
        final var report = Report.newReport(reportedUserId, reportedById, ReportReason.OTHER, null);

        when(reportGateway.findById(any())).thenReturn(Optional.of(report));
        when(reportGateway.save(any(Report.class))).thenAnswer(returnsFirstArg());

        final var command = ResolveReportCommand.with(
                report.getId().getValue(), "APPROVED", "Aprovado sem ban.", 0, adminId);

        // Act
        final var output = useCase.execute(command);

        // Assert
        assertNotNull(output);
        assertEquals("APPROVED", output.status());
        verify(userGateway, never()).findById(any());
        verify(userGateway, never()).save(any());
    }
}
