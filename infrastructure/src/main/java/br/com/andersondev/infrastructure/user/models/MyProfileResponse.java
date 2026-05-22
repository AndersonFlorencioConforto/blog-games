package br.com.andersondev.infrastructure.user.models;

import br.com.andersondev.application.user.profile.MyProfileOutput;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Dados do usuario autenticado (GET /users/me), incluindo email e role.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyProfileResponse(
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

    public static MyProfileResponse from(final MyProfileOutput output) {
        return new MyProfileResponse(
                output.id(),
                output.name(),
                output.email(),
                output.bio(),
                output.avatarUrl(),
                output.role(),
                output.followersCount(),
                output.followingCount(),
                output.createdAt()
        );
    }
}
