package br.com.andersondev.application.user.register;

import br.com.andersondev.domain.user.User;

import java.time.Instant;

/**
 * Saida do cadastro de usuario (espelha o contrato POST /auth/register).
 */
public record RegisterUserOutput(
        String id,
        String name,
        String email,
        String role,
        Instant createdAt
) {

    public static RegisterUserOutput from(final User user) {
        return new RegisterUserOutput(
                user.getId().getValue(),
                user.getName(),
                user.getEmail().getValue(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
