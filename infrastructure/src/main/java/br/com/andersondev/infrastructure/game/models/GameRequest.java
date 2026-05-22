package br.com.andersondev.infrastructure.game.models;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Corpo de requisicao para criar/atualizar jogo (POST /games, PUT /games/{id}).
 * Validacao de formato/obrigatoriedade (INPUT); regras de dominio nos VOs/validador.
 */
public record GameRequest(
        @NotBlank(message = "'title' e obrigatorio")
        @Size(min = 1, max = 200, message = "'title' deve ter entre 1 e 200 caracteres")
        String title,

        @NotBlank(message = "'description' e obrigatorio")
        @Size(min = 1, max = 5000, message = "'description' deve ter entre 1 e 5000 caracteres")
        String description,

        @Size(max = 10000, message = "'editorialReview' deve ter no maximo 10000 caracteres")
        String editorialReview,

        @NotNull(message = "'platformScore' e obrigatorio")
        @DecimalMin(value = "0.0", message = "'platformScore' deve estar entre 0.0 e 10.0")
        @DecimalMax(value = "10.0", message = "'platformScore' deve estar entre 0.0 e 10.0")
        BigDecimal platformScore,

        @Size(max = 10, message = "'pros' deve ter no maximo 10 itens")
        List<String> pros,

        @Size(max = 10, message = "'cons' deve ter no maximo 10 itens")
        List<String> cons,

        @Size(max = 500, message = "'coverImageUrl' deve ter no maximo 500 caracteres")
        String coverImageUrl,

        @NotEmpty(message = "ao menos uma categoria e obrigatoria")
        List<String> categories,

        @NotEmpty(message = "ao menos uma plataforma e obrigatoria")
        List<String> platforms
) {
}
