package br.com.andersondev.infrastructure.shelf.models;

import jakarta.validation.constraints.NotBlank;

/**
 * Corpo da requisicao para adicionar um jogo a estante (POST /shelf).
 * O valor exato do status e validado no dominio (S-01).
 */
public record AddToShelfRequest(
        @NotBlank(message = "'gameId' e obrigatorio")
        String gameId,

        @NotBlank(message = "'status' e obrigatorio")
        String status
) {
}
