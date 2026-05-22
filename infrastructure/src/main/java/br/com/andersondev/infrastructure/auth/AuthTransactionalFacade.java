package br.com.andersondev.infrastructure.auth;

import br.com.andersondev.application.auth.AuthTokensOutput;
import br.com.andersondev.application.auth.login.LoginCommand;
import br.com.andersondev.application.auth.login.LoginUseCase;
import br.com.andersondev.application.auth.logout.LogoutCommand;
import br.com.andersondev.application.auth.logout.LogoutUseCase;
import br.com.andersondev.application.auth.password.RequestPasswordResetCommand;
import br.com.andersondev.application.auth.password.RequestPasswordResetUseCase;
import br.com.andersondev.application.auth.password.ResetPasswordCommand;
import br.com.andersondev.application.auth.password.ResetPasswordUseCase;
import br.com.andersondev.application.auth.refresh.RefreshTokenCommand;
import br.com.andersondev.application.auth.refresh.RefreshTokenUseCase;
import br.com.andersondev.application.user.register.RegisterUserCommand;
import br.com.andersondev.application.user.register.RegisterUserOutput;
import br.com.andersondev.application.user.register.RegisterUserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aplica as fronteiras transacionais (transaction-boundaries) sobre os casos de uso
 * de autenticacao, que sao Java puro (sem Spring) na camada application.
 * Mantem o controller enxuto e a regra de negocio no caso de uso.
 */
@Service
public class AuthTransactionalFacade {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    public AuthTransactionalFacade(
            final RegisterUserUseCase registerUserUseCase,
            final LoginUseCase loginUseCase,
            final RefreshTokenUseCase refreshTokenUseCase,
            final LogoutUseCase logoutUseCase,
            final RequestPasswordResetUseCase requestPasswordResetUseCase,
            final ResetPasswordUseCase resetPasswordUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    @Transactional
    public RegisterUserOutput register(final RegisterUserCommand command) {
        return this.registerUserUseCase.execute(command);
    }

    @Transactional
    public AuthTokensOutput login(final LoginCommand command) {
        return this.loginUseCase.execute(command);
    }

    @Transactional
    public AuthTokensOutput refresh(final RefreshTokenCommand command) {
        return this.refreshTokenUseCase.execute(command);
    }

    @Transactional
    public void logout(final LogoutCommand command) {
        this.logoutUseCase.execute(command);
    }

    @Transactional
    public void requestPasswordReset(final RequestPasswordResetCommand command) {
        this.requestPasswordResetUseCase.execute(command);
    }

    @Transactional
    public void resetPassword(final ResetPasswordCommand command) {
        this.resetPasswordUseCase.execute(command);
    }
}
