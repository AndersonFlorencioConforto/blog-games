package br.com.andersondev.infrastructure.auth.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "'name' e obrigatorio")
        @Size(min = 2, max = 100, message = "'name' deve ter entre 2 e 100 caracteres")
        String name,

        @NotBlank(message = "'email' e obrigatorio")
        @Email(message = "'email' possui formato invalido")
        String email,

        @NotBlank(message = "'password' e obrigatorio")
        @Size(min = 8, message = "'password' deve ter no minimo 8 caracteres")
        String password
) {
}
