package br.com.andersondev.domain.auth.port;

import br.com.andersondev.domain.auth.RefreshToken;
import br.com.andersondev.domain.user.UserId;

import java.util.Optional;

/**
 * Porta de saida para persistencia de refresh tokens.
 */
public interface RefreshTokenGateway {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void revokeAllByUserId(UserId userId);
}
