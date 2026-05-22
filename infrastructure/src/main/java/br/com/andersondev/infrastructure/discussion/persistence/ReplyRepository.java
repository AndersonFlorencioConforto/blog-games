package br.com.andersondev.infrastructure.discussion.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyJpaEntity, String> {

    Page<ReplyJpaEntity> findByThreadId(String threadId, Pageable pageable);
}
