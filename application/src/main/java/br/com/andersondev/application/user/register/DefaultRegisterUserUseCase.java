package br.com.andersondev.application.user.register;

import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.NotificationException;
import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.PasswordPolicy;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.port.PasswordHasherPort;
import br.com.andersondev.domain.user.port.UserGateway;
import br.com.andersondev.domain.validation.handler.Notification;

import java.util.Objects;

/**
 * Cadastro de usuario (U-01..U-06).
 * Fluxo:
 *  1. Valida email (VO), politica de senha e invariantes do User.
 *  2. Verifica unicidade de email (U-03 -> 409).
 *  3. Hasheia a senha (BCrypt 12) e persiste.
 */
public final class DefaultRegisterUserUseCase extends RegisterUserUseCase {

    private final UserGateway userGateway;
    private final PasswordHasherPort passwordHasher;

    public DefaultRegisterUserUseCase(
            final UserGateway userGateway,
            final PasswordHasherPort passwordHasher
    ) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
    }

    @Override
    public RegisterUserOutput execute(final RegisterUserCommand command) {
        final var notification = Notification.create();

        final var email = notification.validate(() -> Email.of(command.email()));
        notification.validate(() -> {
            PasswordPolicy.validate(command.password());
            return null;
        });

        // Constroi e valida o agregado apenas quando email valido (evita NPE em VO nulo).
        final User user = email == null
                ? null
                : notification.validate(() -> buildAndValidate(command));

        if (notification.hasError()) {
            throw new NotificationException("Nao foi possivel cadastrar o usuario", notification);
        }

        if (this.userGateway.existsByEmail(email)) {
            throw DuplicateEntityException.with("Email ja cadastrado");
        }

        final var saved = this.userGateway.save(user);
        return RegisterUserOutput.from(saved);
    }

    private User buildAndValidate(final RegisterUserCommand command) {
        final var passwordHash = this.passwordHasher.hash(command.password());
        final var user = User.newUser(command.name(), command.email(), passwordHash);
        final var validation = Notification.create();
        user.validate(validation);
        if (validation.hasError()) {
            throw new NotificationException("Dados de usuario invalidos", validation);
        }
        return user;
    }
}
