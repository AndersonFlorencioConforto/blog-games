package br.com.andersondev.infrastructure.social.persistence;

/**
 * Projecao de usuario resumido (id, name, avatarUrl) para listagens sociais,
 * preenchida por JPQL com {@code SELECT new ...}.
 */
public record UserSummaryProjection(String id, String name, String avatarUrl) {
}
