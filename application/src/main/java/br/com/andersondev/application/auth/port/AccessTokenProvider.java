package br.com.andersondev.application.auth.port;

import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.UserId;

/**
 * Porta de saida para emissao de access tokens JWT (HS256).
 * Implementada na infrastructure com JJWT.
 */
public interface AccessTokenProvider {

    AccessToken generate(UserId userId, String email, Role role);

    /**
     * Valida assinatura e expiracao do token e extrai as claims.
     * Lanca {@link br.com.andersondev.domain.exception.TokenExpiredException}
     * para token invalido ou expirado.
     */
    AccessTokenClaims parse(String token);
}
