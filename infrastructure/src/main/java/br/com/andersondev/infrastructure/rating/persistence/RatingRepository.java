package br.com.andersondev.infrastructure.rating.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingJpaEntity, String> {

    Optional<RatingJpaEntity> findByGameIdAndUserId(String gameId, String userId);

    /**
     * Recalcula media (AVG) e total (COUNT) das avaliacoes de um jogo.
     * Fonte de verdade da media desnormalizada (data-strategy secao 5).
     */
    @Query("""
            SELECT new br.com.andersondev.infrastructure.rating.persistence.RatingAggregateProjection(
                AVG(r.stars), COUNT(r))
            FROM Rating r
            WHERE r.gameId = :gameId
            """)
    RatingAggregateProjection aggregateByGameId(@Param("gameId") String gameId);
}
