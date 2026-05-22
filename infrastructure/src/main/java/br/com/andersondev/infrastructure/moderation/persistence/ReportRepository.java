package br.com.andersondev.infrastructure.moderation.persistence;

import br.com.andersondev.domain.moderation.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data para ReportJpaEntity.
 */
public interface ReportRepository extends JpaRepository<ReportJpaEntity, String> {

    Page<ReportJpaEntity> findByStatus(ReportStatus status, Pageable pageable);

    boolean existsByReportedByIdAndReportedUserIdAndStatus(
            String reportedById,
            String reportedUserId,
            ReportStatus status
    );
}
