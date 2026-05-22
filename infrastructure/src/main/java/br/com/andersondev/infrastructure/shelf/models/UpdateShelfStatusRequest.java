package br.com.andersondev.infrastructure.shelf.models;

import jakarta.validation.constraints.NotBlank;

/**
 * Corpo da requisicao para atualizar o status de um item da estante (PUT /shelf/{gameId}).
 */
public record UpdateShelfStatusRequest(
        @NotBlank(message = "'status' e obrigatorio")
        String status
) {
}
