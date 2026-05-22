package br.com.andersondev.infrastructure.game.persistence;

import br.com.andersondev.domain.game.Category;
import br.com.andersondev.domain.game.GameSummary;
import br.com.andersondev.domain.game.Platform;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<GameJpaEntity, String> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, String id);

    /**
     * Carrega o jogo com lock pessimista de escrita (SELECT FOR UPDATE) para
     * serializar a atualizacao transacional da media (RateGameUseCase).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Game g WHERE g.id = :id")
    Optional<GameJpaEntity> findByIdForUpdate(@Param("id") String id);

    // --- Busca paginada de jogos nao deletados (G-09), com filtros opcionais. ---
    // Queries separadas por combinacao de filtros para evitar comparacao de
    // parametros nulos em JPQL (portabilidade H2/PostgreSQL).

    @Query("SELECT g FROM Game g WHERE g.deletedAt IS NULL")
    Page<GameJpaEntity> findAllActive(Pageable pageable);

    @Query("SELECT DISTINCT g FROM Game g "
            + "WHERE g.deletedAt IS NULL AND :category MEMBER OF g.categories")
    Page<GameJpaEntity> findActiveByCategory(@Param("category") Category category, Pageable pageable);

    @Query("SELECT DISTINCT g FROM Game g "
            + "WHERE g.deletedAt IS NULL AND :platform MEMBER OF g.platforms")
    Page<GameJpaEntity> findActiveByPlatform(@Param("platform") Platform platform, Pageable pageable);

    @Query("SELECT DISTINCT g FROM Game g "
            + "WHERE g.deletedAt IS NULL "
            + "AND :category MEMBER OF g.categories "
            + "AND :platform MEMBER OF g.platforms")
    Page<GameJpaEntity> findActiveByCategoryAndPlatform(
            @Param("category") Category category,
            @Param("platform") Platform platform,
            Pageable pageable);

    /**
     * Dados resumidos (id, title, coverImageUrl) de jogos nao deletados, por ids.
     * Usado para enriquecer listagens cross-aggregate (ex.: estante).
     */
    @Query("""
            SELECT new br.com.andersondev.domain.game.GameSummary(g.id, g.title, g.coverImageUrl)
            FROM Game g
            WHERE g.id IN :ids AND g.deletedAt IS NULL
            """)
    List<GameSummary> findSummariesByIds(@Param("ids") Collection<String> ids);
}
