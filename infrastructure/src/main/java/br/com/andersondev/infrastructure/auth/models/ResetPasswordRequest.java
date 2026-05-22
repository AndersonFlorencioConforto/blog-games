package br.com.andersondev.infrastructure.auth.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "'token' e obrigatorio")
        String token,

        @NotBlank(message = "'newPassword' e obrigatorio")
        @Size(min = 8, message = "'newPassword' deve ter no minimo 8 caracteres")
        String newPassword
) {
}
