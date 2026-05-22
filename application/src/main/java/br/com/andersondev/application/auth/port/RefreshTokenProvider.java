package br.com.andersondev.application.auth.port;

import java.time.Instant;

/**
 * Porta de saida para geracao de refresh tokens opacos (UUID) e seu hash (SHA-256).
 * Implementada na infrastructure.
 */
public interface RefreshTokenProvider {

    /**
     * Gera um novo refresh token opaco (valor cru entregue ao cliente) com seu hash
     * e a data de expiracao (7 dias).
     */
    GeneratedRefreshToken generate();

    /**
     * Calcula o hash (SHA-256) de um valor cru para lookup na base.
     */
    String hash(String rawToken);

    record GeneratedRefreshToken(String rawValue, String tokenHash, Instant expiresAt) {
    }
}
