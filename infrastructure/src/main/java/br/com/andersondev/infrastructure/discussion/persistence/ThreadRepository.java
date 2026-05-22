package br.com.andersondev.infrastructure.discussion.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends JpaRepository<ThreadJpaEntity, String> {

    Page<ThreadJpaEntity> findByGameId(String gameId, Pageable pageable);
}
