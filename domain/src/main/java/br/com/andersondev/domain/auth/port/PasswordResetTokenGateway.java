package br.com.andersondev.domain.auth.port;

import br.com.andersondev.domain.auth.PasswordResetToken;

import java.util.Optional;

/**
 * Porta de saida para persistencia de tokens de recuperacao de senha.
 */
public interface PasswordResetTokenGateway {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    int deleteExpired();
}
