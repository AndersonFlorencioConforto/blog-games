package br.com.andersondev.infrastructure.auth.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracao do envio de e-mail. Em local, {@code enabled=false} faz o adapter
 * apenas logar o link de reset (sem SMTP) - configuravel e explicito (P-006).
 */
@ConfigurationProperties(prefix = "app.mail")
public class EmailProperties {

    /** Quando false, o adapter apenas loga o link (modo local sem SMTP). */
    private boolean enabled = false;

    /** Remetente dos e-mails. */
    private String from = "no-reply@blogames.com";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }
}
