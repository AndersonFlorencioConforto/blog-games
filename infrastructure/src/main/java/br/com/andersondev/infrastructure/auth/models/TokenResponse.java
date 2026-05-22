package br.com.andersondev.infrastructure.auth.models;

import br.com.andersondev.application.auth.AuthTokensOutput;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {

    public static TokenResponse from(final AuthTokensOutput output) {
        return new TokenResponse(
                output.accessToken(), output.refreshToken(), output.tokenType(), output.expiresIn());
    }
}
