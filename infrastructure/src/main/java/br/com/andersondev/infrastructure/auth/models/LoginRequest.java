package br.com.andersondev.infrastructure.auth.models;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "'email' e obrigatorio")
        String email,

        @NotBlank(message = "'password' e obrigatorio")
        String password
) {
}
