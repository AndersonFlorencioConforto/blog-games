package br.com.andersondev.infrastructure.auth.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TokenBlocklistRepository extends JpaRepository<TokenBlocklistJpaEntity, String> {

    boolean existsByJti(String jti);

    @Modifying
    @Query("DELETE FROM TokenBlocklist t WHERE t.expiresAt < :now")
    int deleteExpired(@Param("now") Instant now);
}
