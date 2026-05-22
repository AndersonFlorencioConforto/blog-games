package br.com.andersondev.infrastructure.auth;

import br.com.andersondev.domain.auth.port.PasswordResetTokenGateway;
import br.com.andersondev.domain.auth.port.TokenBlocklistGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Limpeza de tokens expirados (data-strategy secao 6 / ADR-0003 consequencias).
 * Executa diariamente as 3h.
 */
@Component
public class TokenCleanupJob {

    private static final Logger LOG = LoggerFactory.getLogger(TokenCleanupJob.class);

    private final TokenBlocklistGateway tokenBlocklistGateway;
    private final PasswordResetTokenGateway passwordResetTokenGateway;

    public TokenCleanupJob(
            final TokenBlocklistGateway tokenBlocklistGateway,
            final PasswordResetTokenGateway passwordResetTokenGateway
    ) {
        this.tokenBlocklistGateway = tokenBlocklistGateway;
        this.passwordResetTokenGateway = passwordResetTokenGateway;
    }

    @Scheduled(cron = "${app.cleanup.cron:0 0 3 * * *}")
    public void cleanup() {
        final var removedBlocklist = this.tokenBlocklistGateway.deleteExpired();
        final var removedResets = this.passwordResetTokenGateway.deleteExpired();
        LOG.info("Limpeza de tokens concluida: {} da denylist, {} de reset", removedBlocklist, removedResets);
    }
}
