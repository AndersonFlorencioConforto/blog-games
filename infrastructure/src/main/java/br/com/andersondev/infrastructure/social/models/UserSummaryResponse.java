package br.com.andersondev.infrastructure.social.models;

import br.com.andersondev.application.social.UserSummaryOutput;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Usuario resumido em listagens sociais (seguidores/seguindo).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserSummaryResponse(String id, String name, String avatarUrl) {

    public static UserSummaryResponse from(final UserSummaryOutput output) {
        return new UserSummaryResponse(output.id(), output.name(), output.avatarUrl());
    }
}
