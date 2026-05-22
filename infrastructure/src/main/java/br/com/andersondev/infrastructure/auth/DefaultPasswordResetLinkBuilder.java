package br.com.andersondev.infrastructure.auth;

import br.com.andersondev.application.auth.port.PasswordResetLinkBuilder;
import br.com.andersondev.infrastructure.config.PasswordResetProperties;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class DefaultPasswordResetLinkBuilder implements PasswordResetLinkBuilder {

    private final PasswordResetProperties properties;

    public DefaultPasswordResetLinkBuilder(final PasswordResetProperties properties) {
        this.properties = properties;
    }

    @Override
    public String build(final String rawToken) {
        final var base = this.properties.getAppBaseUrl().replaceAll("/+$", "");
        final var path = this.properties.getResetPath();
        final var encodedToken = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        return "%s%s?token=%s".formatted(base, path, encodedToken);
    }
}
