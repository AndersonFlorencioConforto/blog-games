package br.com.andersondev.infrastructure.discussion.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadLikeRepository extends JpaRepository<ThreadLikeJpaEntity, ThreadLikeId> {

    boolean existsByIdThreadIdAndIdUserId(String threadId, String userId);

    @Modifying
    void deleteByIdThreadIdAndIdUserId(String threadId, String userId);
}
