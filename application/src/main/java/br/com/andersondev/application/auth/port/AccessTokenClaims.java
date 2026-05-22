package br.com.andersondev.application.auth.port;

import java.time.Instant;

/**
 * Claims extraidas de um access token JWT validado.
 */
public record AccessTokenClaims(
        String userId,
        String email,
        String role,
        String jti,
        Instant expiresAt
) {
}
