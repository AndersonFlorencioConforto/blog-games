package br.com.andersondev.infrastructure.user.models;

import br.com.andersondev.application.user.profile.UserProfileOutput;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Perfil publico de um usuario (GET /users/{id} e PUT /users/me).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfileResponse(
        String id,
        String name,
        String bio,
        String avatarUrl,
        long followersCount,
        long followingCount,
        Instant createdAt
) {

    public static UserProfileResponse from(final UserProfileOutput output) {
        return new UserProfileResponse(
                output.id(),
                output.name(),
                output.bio(),
                output.avatarUrl(),
                output.followersCount(),
                output.followingCount(),
                output.createdAt()
        );
    }
}
