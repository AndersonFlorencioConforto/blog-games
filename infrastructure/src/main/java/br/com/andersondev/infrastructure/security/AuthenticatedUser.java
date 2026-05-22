package br.com.andersondev.infrastructure.security;

import java.time.Instant;

/**
 * Principal autenticado, exposto no SecurityContext apos validacao do JWT.
 */
public record AuthenticatedUser(
        String userId,
        String email,
        String role,
        String jti,
        Instant accessTokenExpiresAt
) {
}
