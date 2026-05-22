package br.com.andersondev.application.moderation.create;

import br.com.andersondev.domain.exception.BusinessRuleViolationException;
import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.moderation.Report;
import br.com.andersondev.domain.moderation.port.ReportGateway;
import br.com.andersondev.domain.user.Email;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCreateReportUseCaseTest {

    @Mock
    private ReportGateway reportGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private DefaultCreateReportUseCase useCase;

    @Test
    void givenValidCommand_whenExecute_thenReturnOutput() {
        // Arrange
        final var reportedUserId = UserId.unique().getValue();
        final var reportedById = UserId.unique().getValue();
        final var command = CreateReportCommand.with(reportedUserId, reportedById, "SPAM", "Enviando spam.");

        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(stubUser()));
        when(reportGateway.existsPendingReport(any(UserId.class), any(UserId.class))).thenReturn(false);
        when(reportGateway.save(any(Report.class))).thenAnswer(returnsFirstArg());

        // Act
        final var output = useCase.execute(command);

        // Assert
        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals("SPAM", output.reason());
        assertEquals("PENDING", output.status());
        verify(reportGateway).save(any(Report.class));
    }

    @Test
    void givenNonExistentReportedUser_whenExecute_thenThrowNotFoundException() {
        // Arrange
        final var reportedUserId = UserId.unique().getValue();
        final var reportedById = UserId.unique().getValue();
        final var command = CreateReportCommand.with(reportedUserId, reportedById, "SPAM", null);

        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(reportGateway, never()).save(any());
    }

    @Test
    void givenExistingPendingReport_whenExecute_thenThrowException() {
        // Arrange
        final var reportedUserId = UserId.unique().getValue();
        final var reportedById = UserId.unique().getValue();
        final var command = CreateReportCommand.with(reportedUserId, reportedById, "HARASSMENT", null);

        when(userGateway.findById(any(UserId.class))).thenReturn(Optional.of(stubUser()));
        when(reportGateway.existsPendingReport(any(UserId.class), any(UserId.class))).thenReturn(true);

        // Act + Assert
        final var ex = assertThrows(BusinessRuleViolationException.class, () -> useCase.execute(command));
        assertTrue(ex.getMessage().contains("denuncia pendente"));
        verify(reportGateway, never()).save(any());
    }

    @Test
    void givenSameUserId_whenExecute_thenThrowDomainException() {
        // Arrange
        final var sameId = UserId.unique().getValue();
        final var command = CreateReportCommand.with(sameId, sameId, "SPAM", null);

        // Act + Assert
        final var ex = assertThrows(DomainException.class, () -> useCase.execute(command));
        assertTrue(ex.getMessage().contains("nao pode denunciar a si mesmo"));
        verify(userGateway, never()).findById(any());
        verify(reportGateway, never()).save(any());
    }

    private static User stubUser() {
        return User.newUser("Usuario Teste", "teste@email.com", "hashedpassword123");
    }
}
