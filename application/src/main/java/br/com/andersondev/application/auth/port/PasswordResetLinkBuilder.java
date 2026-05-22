package br.com.andersondev.application.auth.port;

/**
 * Porta de saida para montagem do link de recuperacao de senha enviado por e-mail.
 * Depende de APP_BASE_URL (configuracao de infrastructure).
 */
public interface PasswordResetLinkBuilder {

    String build(String rawToken);
}
