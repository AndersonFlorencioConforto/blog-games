package br.com.andersondev.infrastructure.shelf.persistence;

import br.com.andersondev.domain.shelf.ShelfStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShelfItemRepository extends JpaRepository<ShelfItemJpaEntity, String> {

    Optional<ShelfItemJpaEntity> findByUserIdAndGameId(String userId, String gameId);

    boolean existsByUserIdAndGameId(String userId, String gameId);

    @Modifying
    void deleteByUserIdAndGameId(String userId, String gameId);

    @Query("SELECT s FROM ShelfItem s WHERE s.userId = :userId")
    Page<ShelfItemJpaEntity> findByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT s FROM ShelfItem s WHERE s.userId = :userId AND s.status = :status")
    Page<ShelfItemJpaEntity> findByUserIdAndStatus(
            @Param("userId") String userId,
            @Param("status") ShelfStatus status,
            Pageable pageable);
}
