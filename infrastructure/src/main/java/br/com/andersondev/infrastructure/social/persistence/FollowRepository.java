package br.com.andersondev.infrastructure.social.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowJpaEntity, FollowId> {

    boolean existsByFollowerIdAndFollowedId(String followerId, String followedId);

    @Modifying
    void deleteByFollowerIdAndFollowedId(String followerId, String followedId);

    long countByFollowedId(String followedId);

    long countByFollowerId(String followerId);

    /**
     * Seguidores de {@code followedId}: usuarios cujo id e follower em follows(followed_id).
     * Ordenado pela data do follow (mais recentes primeiro).
     */
    @Query("""
            SELECT new br.com.andersondev.infrastructure.social.persistence.UserSummaryProjection(
                u.id, u.name, u.avatarUrl)
            FROM Follow f
            JOIN User u ON u.id = f.followerId
            WHERE f.followedId = :followedId
            ORDER BY f.followedAt DESC
            """)
    Page<UserSummaryProjection> findFollowers(@Param("followedId") String followedId, Pageable pageable);

    /**
     * Seguindo de {@code followerId}: usuarios cujo id e followed em follows(follower_id).
     */
    @Query("""
            SELECT new br.com.andersondev.infrastructure.social.persistence.UserSummaryProjection(
                u.id, u.name, u.avatarUrl)
            FROM Follow f
            JOIN User u ON u.id = f.followedId
            WHERE f.followerId = :followerId
            ORDER BY f.followedAt DESC
            """)
    Page<UserSummaryProjection> findFollowing(@Param("followerId") String followerId, Pageable pageable);
}
