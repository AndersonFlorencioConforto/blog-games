package br.com.andersondev.domain.moderation.port;

import br.com.andersondev.domain.moderation.Report;
import br.com.andersondev.domain.moderation.ReportId;
import br.com.andersondev.domain.moderation.ReportStatus;
import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.user.UserId;

import java.util.Optional;

/**
 * Porta de saida para persistencia de denuncias.
 * Implementada na infrastructure (adapter JPA).
 */
public interface ReportGateway {

    Report save(Report report);

    Optional<Report> findById(ReportId id);

    /**
     * Retorna denuncias paginadas. Se status for null, retorna todas.
     */
    Pagination<Report> findAll(ReportStatus status, int page, int size);

    /**
     * Verifica se ja existe denuncia PENDING do mesmo denunciante para o mesmo denunciado.
     */
    boolean existsPendingReport(UserId reportedById, UserId reportedUserId);
}
