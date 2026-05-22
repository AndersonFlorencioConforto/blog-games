package br.com.andersondev.infrastructure.auth.models;

/**
 * Corpo opcional do logout; permite revogar um refresh token especifico.
 */
public record LogoutRequest(String refreshToken) {
}
