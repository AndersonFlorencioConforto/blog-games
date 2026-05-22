package br.com.andersondev.application.auth.port;

import java.time.Instant;

/**
 * Representa um access token JWT emitido, com seu jti e expiracao.
 */
public record AccessToken(String token, String jti, Instant expiresAt, long expiresInSeconds) {
}
