package br.com.andersondev.domain.user;

/**
 * Projecao resumida de usuario para listagens (seguidores/seguindo).
 * Espelha o contrato HTTP: {@code id, name, avatarUrl} (api-catalog secao 2).
 */
public record UserSummary(String id, String name, String avatarUrl) {

    public static UserSummary of(final String id, final String name, final String avatarUrl) {
        return new UserSummary(id, name, avatarUrl);
    }
}
