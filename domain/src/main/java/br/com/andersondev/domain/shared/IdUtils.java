package br.com.andersondev.domain.shared;

import java.util.UUID;

/**
 * Utilitario de geracao de identificadores UUID v4.
 */
public final class IdUtils {

    private IdUtils() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
