package br.com.andersondev.application.user.profile;

import br.com.andersondev.domain.user.User;

import java.time.Instant;

/**
 * Saida do perfil publico de um usuario (espelha GET /users/{id}).
 * {@code followersCount}/{@code followingCount} sao derivados do FollowGateway.
 */
public record UserProfileOutput(
        String id,
        String name,
        String bio,
        String avatarUrl,
        long followersCount,
        long followingCount,
        Instant createdAt
) {

    public static UserProfileOutput from(final User user, final long followersCount, final long followingCount) {
        return new UserProfileOutput(
                user.getId().getValue(),
                user.getName(),
                user.getBio(),
                user.getAvatarUrl(),
                followersCount,
                followingCount,
                user.getCreatedAt()
        );
    }
}
