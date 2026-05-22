package br.com.andersondev.application.auth.logout;

import java.time.Instant;

/**
 * Comando de logout. O jti e a expiracao sao extraidos do access token ja validado no filtro.
 * O refreshToken (opcional) e o enviado pelo cliente para revogacao especifica.
 */
public record LogoutCommand(
        String userId,
        String jti,
        Instant accessTokenExpiresAt,
        String refreshToken
) {

    public static LogoutCommand with(
            final String userId,
            final String jti,
            final Instant accessTokenExpiresAt,
            final String refreshToken
    ) {
        return new LogoutCommand(userId, jti, accessTokenExpiresAt, refreshToken);
    }
}
