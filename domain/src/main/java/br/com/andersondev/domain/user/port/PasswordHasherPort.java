package br.com.andersondev.domain.user.port;

/**
 * Porta de saida para hash de senhas (BCrypt fator 12).
 * Implementada na infrastructure.
 */
public interface PasswordHasherPort {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
