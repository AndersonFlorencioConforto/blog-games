package br.com.andersondev.infrastructure.auth.models;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "'refreshToken' e obrigatorio")
        String refreshToken
) {
}
