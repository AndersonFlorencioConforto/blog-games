package br.com.andersondev.infrastructure.auth.email;

import br.com.andersondev.domain.auth.port.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Adapter de envio de e-mail sincrono (ADR-0004 / P-006).
 * Se {@code app.mail.enabled=false} (default local), apenas loga o link em vez de enviar.
 * Falha de SMTP e logada e nao propaga (best-effort, transaction-boundaries secao 5).
 */
@Component
public class JavaMailEmailAdapter implements EmailPort {

    private static final Logger LOG = LoggerFactory.getLogger(JavaMailEmailAdapter.class);

    private final EmailProperties properties;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public JavaMailEmailAdapter(
            final EmailProperties properties,
            final ObjectProvider<JavaMailSender> mailSenderProvider
    ) {
        this.properties = properties;
        this.mailSenderProvider = mailSenderProvider;
    }

    @Override
    public void sendPasswordResetEmail(final String toEmail, final String resetLink) {
        final var mailSender = this.mailSenderProvider.getIfAvailable();

        if (!this.properties.isEnabled() || mailSender == null) {
            LOG.info("[MAIL DISABLED] Link de recuperacao de senha para {}: {}", toEmail, resetLink);
            return;
        }

        try {
            final var message = new SimpleMailMessage();
            message.setFrom(this.properties.getFrom());
            message.setTo(toEmail);
            message.setSubject("Recuperacao de senha - Blog Games Platform");
            message.setText("Recebemos uma solicitacao de recuperacao de senha.\n\n"
                    + "Acesse o link a seguir (valido por 1 hora):\n" + resetLink
                    + "\n\nSe voce nao solicitou, ignore este e-mail.");
            mailSender.send(message);
        } catch (final RuntimeException ex) {
            // Falha de SMTP nao deve quebrar o fluxo; usuario pode solicitar novamente.
            LOG.warn("Falha ao enviar e-mail de recuperacao para {}: {}", toEmail, ex.getMessage());
        }
    }
}
