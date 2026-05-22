package br.com.andersondev.infrastructure.auth.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "'email' e obrigatorio")
        @Email(message = "'email' possui formato invalido")
        String email
) {
}
