package br.com.andersondev.domain.auth.port;

/**
 * Porta de saida para envio de e-mail. Implementada na infrastructure
 * (JavaMailSender sincrono - ADR-0004 / P-006; em local pode apenas logar).
 */
public interface EmailPort {

    void sendPasswordResetEmail(String toEmail, String resetLink);
}
