package br.com.andersondev.application.auth.password;

import br.com.andersondev.application.auth.port.PasswordResetLinkBuilder;
import br.com.andersondev.application.auth.port.ResetTokenProvider;
import br.com.andersondev.domain.auth.PasswordResetToken;
import br.com.andersondev.domain.auth.port.EmailPort;
import br.com.andersondev.domain.auth.port.PasswordResetTokenGateway;
import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.port.UserGateway;

import java.util.Objects;

/**
 * Solicitacao de recuperacao de senha (P-01).
 * Sempre conclui sem erro para nao revelar se o email existe (controller retorna 202).
 * Se o usuario existir: gera token (hash 1h), persiste e envia e-mail sincrono.
 *
 * O envio de e-mail e best-effort: falha de SMTP nao deve impedir a operacao
 * (transaction-boundaries secao 5). Aqui logamos a falha e seguimos.
 */
public final class DefaultRequestPasswordResetUseCase extends RequestPasswordResetUseCase {

    private final UserGateway userGateway;
    private final PasswordResetTokenGateway passwordResetTokenGateway;
    private final ResetTokenProvider resetTokenProvider;
    private final PasswordResetLinkBuilder linkBuilder;
    private final EmailPort emailPort;

    public DefaultRequestPasswordResetUseCase(
            final UserGateway userGateway,
            final PasswordResetTokenGateway passwordResetTokenGateway,
            final ResetTokenProvider resetTokenProvider,
            final PasswordResetLinkBuilder linkBuilder,
            final EmailPort emailPort
    ) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.passwordResetTokenGateway = Objects.requireNonNull(passwordResetTokenGateway);
        this.resetTokenProvider = Objects.requireNonNull(resetTokenProvider);
        this.linkBuilder = Objects.requireNonNull(linkBuilder);
        this.emailPort = Objects.requireNonNull(emailPort);
    }

    @Override
    public void execute(final RequestPasswordResetCommand command) {
        final Email email;
        try {
            email = Email.of(command.email());
        } catch (final RuntimeException ex) {
            // Email malformado: silenciosamente ignora (nao revela existencia).
            return;
        }

        this.userGateway.findByEmail(email).ifPresent(user -> {
            final var generated = this.resetTokenProvider.generate();
            this.passwordResetTokenGateway.save(PasswordResetToken.issue(
                    user.getId(), generated.tokenHash(), generated.expiresAt()));

            final var resetLink = this.linkBuilder.build(generated.rawValue());
            this.emailPort.sendPasswordResetEmail(user.getEmail().getValue(), resetLink);
        });
    }
}
