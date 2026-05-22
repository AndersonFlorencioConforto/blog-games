package br.com.andersondev.application.auth.port;

import java.time.Instant;

/**
 * Porta de saida para geracao de tokens de recuperacao de senha (UUID opaco)
 * e seu hash (SHA-256). Expiracao de 1 hora (ADR-0003).
 */
public interface ResetTokenProvider {

    GeneratedResetToken generate();

    String hash(String rawToken);

    record GeneratedResetToken(String rawValue, String tokenHash, Instant expiresAt) {
    }
}
