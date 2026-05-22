package br.com.andersondev.infrastructure.auth.models;

import br.com.andersondev.application.user.register.RegisterUserOutput;

import java.time.Instant;

public record RegisterResponse(
        String id,
        String name,
        String email,
        String role,
        Instant createdAt
) {

    public static RegisterResponse from(final RegisterUserOutput output) {
        return new RegisterResponse(
                output.id(), output.name(), output.email(), output.role(), output.createdAt());
    }
}
