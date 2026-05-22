package br.com.andersondev.domain.auth.port;

import br.com.andersondev.domain.auth.BlocklistedToken;

/**
 * Porta de saida para a denylist de access tokens (jti).
 */
public interface TokenBlocklistGateway {

    BlocklistedToken save(BlocklistedToken token);

    boolean existsByJti(String jti);

    int deleteExpired();
}
