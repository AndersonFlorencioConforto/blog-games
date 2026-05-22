package br.com.andersondev.infrastructure.user.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Corpo da requisicao para atualizar o perfil do usuario autenticado (PUT /users/me).
 * Validacao de formato (INPUT). PR-01..PR-03.
 */
public record UpdateProfileRequest(
        @NotBlank(message = "'name' e obrigatorio")
        @Size(min = 2, max = 100, message = "'name' deve ter entre 2 e 100 caracteres")
        String name,

        @Size(max = 500, message = "'bio' deve ter no maximo 500 caracteres")
        String bio,

        @Size(max = 500, message = "'avatarUrl' deve ter no maximo 500 caracteres")
        String avatarUrl
) {
}
