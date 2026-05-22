package br.com.andersondev.infrastructure.config;

import br.com.andersondev.infrastructure.auth.email.EmailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Habilita as ConfigurationProperties da aplicacao e o agendamento (jobs de limpeza).
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties({
        JwtProperties.class,
        PasswordResetProperties.class,
        EmailProperties.class
})
public class AppConfig {
}
