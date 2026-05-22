package br.com.andersondev.infrastructure.rating.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo de requisicao para avaliar um jogo (POST /games/{id}/ratings). R-01.
 */
public record RateGameRequest(
        @NotNull(message = "'stars' e obrigatorio")
        @Min(value = 0, message = "'stars' deve ser um inteiro entre 0 e 5")
        @Max(value = 5, message = "'stars' deve ser um inteiro entre 0 e 5")
        Integer stars
) {
}
