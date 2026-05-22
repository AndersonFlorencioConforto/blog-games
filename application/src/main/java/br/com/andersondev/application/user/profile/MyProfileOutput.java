package br.com.andersondev.application.user.profile;

import br.com.andersondev.domain.user.User;

import java.time.Instant;

/**
 * Saida do perfil do usuario autenticado (espelha GET /users/me).
 * Inclui email e role, alem dos contadores sociais derivados do FollowGateway.
 */
public record MyProfileOutput(
        String id,
        String name,
        String email,
        String bio,
        String avatarUrl,
        String role,
        long followersCount,
        long followingCount,
        Instant createdAt
) {

    public static MyProfileOutput from(final User user, final long followersCount, final long followingCount) {
        return new MyProfileOutput(
                user.getId().getValue(),
                user.getName(),
                user.getEmail().getValue(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getRole().name(),
                followersCount,
                followingCount,
                user.getCreatedAt()
        );
    }
}
