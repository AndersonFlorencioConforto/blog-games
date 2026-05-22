package br.com.andersondev.infrastructure.discussion.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyLikeRepository extends JpaRepository<ReplyLikeJpaEntity, ReplyLikeId> {

    boolean existsByIdReplyIdAndIdUserId(String replyId, String userId);

    @Modifying
    void deleteByIdReplyIdAndIdUserId(String replyId, String userId);
}
