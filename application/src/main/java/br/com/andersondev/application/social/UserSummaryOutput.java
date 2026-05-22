package br.com.andersondev.application.social;

import br.com.andersondev.domain.user.UserSummary;

/**
 * Saida resumida de usuario para listagens sociais (seguidores/seguindo).
 * Espelha {@code id, name, avatarUrl} (api-catalog secao 2).
 */
public record UserSummaryOutput(String id, String name, String avatarUrl) {

    public static UserSummaryOutput from(final UserSummary summary) {
        return new UserSummaryOutput(summary.id(), summary.name(), summary.avatarUrl());
    }
}
