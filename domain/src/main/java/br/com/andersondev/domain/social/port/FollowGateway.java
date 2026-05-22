package br.com.andersondev.domain.social.port;

import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.social.Follow;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.UserSummary;

/**
 * Porta de saida para persistencia de relacoes de seguir (domain-catalog 4 / FollowRepository).
 * Implementada na infrastructure (adapter JPA).
 */
public interface FollowGateway {

    Follow save(Follow follow);

    boolean existsByFollowerIdAndFollowedId(UserId followerId, UserId followedId);

    void deleteByFollowerIdAndFollowedId(UserId followerId, UserId followedId);

    /**
     * Quantidade de seguidores de um usuario (quem segue {@code followedId}).
     */
    long countByFollowedId(UserId followedId);

    /**
     * Quantidade de usuarios que {@code followerId} segue.
     */
    long countByFollowerId(UserId followerId);

    /**
     * Lista paginada de seguidores de {@code followedId} (dados resumidos de usuario).
     */
    Pagination<UserSummary> findFollowers(UserId followedId, int page, int size);

    /**
     * Lista paginada de usuarios que {@code followerId} segue (dados resumidos de usuario).
     */
    Pagination<UserSummary> findFollowing(UserId followerId, int page, int size);
}
