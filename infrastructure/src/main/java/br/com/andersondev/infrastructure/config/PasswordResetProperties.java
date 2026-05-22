package br.com.andersondev.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracao do fluxo de recuperacao de senha (ADR-0003).
 */
@ConfigurationProperties(prefix = "security.password-reset")
public class PasswordResetProperties {

    /** Expiracao do token de reset em segundos (default 1h). */
    private long tokenExpiration = 3600;

    /** URL base da aplicacao para montar o link enviado por e-mail (APP_BASE_URL). */
    private String appBaseUrl = "http://localhost:8080";

    /** Caminho relativo do reset no front. */
    private String resetPath = "/reset-password";

    public long getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(final long tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public String getAppBaseUrl() {
        return appBaseUrl;
    }

    public void setAppBaseUrl(final String appBaseUrl) {
        this.appBaseUrl = appBaseUrl;
    }

    public String getResetPath() {
        return resetPath;
    }

    public void setResetPath(final String resetPath) {
        this.resetPath = resetPath;
    }
}
