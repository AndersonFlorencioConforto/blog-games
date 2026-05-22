package br.com.andersondev.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utilitario para hash SHA-256 (usado em refresh tokens e tokens de reset, ADR-0003).
 * Produz string hex de 64 chars compativel com VARCHAR(64).
 */
final class Sha256Hasher {

    private Sha256Hasher() {
    }

    static String hash(final String value) {
        try {
            final var digest = MessageDigest.getInstance("SHA-256");
            final var bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (final NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponivel", ex);
        }
    }
}
