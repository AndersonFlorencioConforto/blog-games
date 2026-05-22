package br.com.andersondev.application.auth;

/**
 * Par de tokens emitido por login/refresh.
 */
public record AuthTokensOutput(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {

    public static AuthTokensOutput of(final String accessToken, final String refreshToken, final long expiresIn) {
        return new AuthTokensOutput(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
