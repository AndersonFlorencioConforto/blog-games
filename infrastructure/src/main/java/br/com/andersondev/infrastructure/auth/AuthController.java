package br.com.andersondev.infrastructure.auth;

import br.com.andersondev.application.auth.login.LoginCommand;
import br.com.andersondev.application.auth.logout.LogoutCommand;
import br.com.andersondev.application.auth.password.RequestPasswordResetCommand;
import br.com.andersondev.application.auth.password.ResetPasswordCommand;
import br.com.andersondev.application.auth.refresh.RefreshTokenCommand;
import br.com.andersondev.application.user.register.RegisterUserCommand;
import br.com.andersondev.infrastructure.auth.models.ForgotPasswordRequest;
import br.com.andersondev.infrastructure.auth.models.LoginRequest;
import br.com.andersondev.infrastructure.auth.models.LogoutRequest;
import br.com.andersondev.infrastructure.auth.models.RefreshRequest;
import br.com.andersondev.infrastructure.auth.models.RegisterRequest;
import br.com.andersondev.infrastructure.auth.models.RegisterResponse;
import br.com.andersondev.infrastructure.auth.models.ResetPasswordRequest;
import br.com.andersondev.infrastructure.auth.models.TokenResponse;
import br.com.andersondev.infrastructure.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Controller de autenticacao (api-catalog secao 1). Enxuto: apenas mapeia HTTP -> command
 * e delega para a fachada transacional. Regras de negocio ficam nos casos de uso.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthTransactionalFacade authFacade;

    public AuthController(final AuthTransactionalFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody final RegisterRequest request) {
        final var output = this.authFacade.register(
                RegisterUserCommand.with(request.name(), request.email(), request.password()));
        return ResponseEntity
                .created(URI.create("/api/v1/users/" + output.id()))
                .body(RegisterResponse.from(output));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody final LoginRequest request,
            final HttpServletRequest httpRequest
    ) {
        final var output = this.authFacade.login(
                LoginCommand.with(request.email(), request.password(), deviceInfo(httpRequest)));
        return ResponseEntity.ok(TokenResponse.from(output));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody final RefreshRequest request,
            final HttpServletRequest httpRequest
    ) {
        final var output = this.authFacade.refresh(
                RefreshTokenCommand.with(request.refreshToken(), deviceInfo(httpRequest)));
        return ResponseEntity.ok(TokenResponse.from(output));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal final AuthenticatedUser principal,
            @RequestBody(required = false) final LogoutRequest request
    ) {
        final var refreshToken = request != null ? request.refreshToken() : null;
        this.authFacade.logout(LogoutCommand.with(
                principal.userId(),
                principal.jti(),
                principal.accessTokenExpiresAt(),
                refreshToken));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody final ForgotPasswordRequest request) {
        this.authFacade.requestPasswordReset(RequestPasswordResetCommand.with(request.email()));
        // Sempre 202 para nao revelar se o email existe (P-01).
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody final ResetPasswordRequest request) {
        this.authFacade.resetPassword(ResetPasswordCommand.with(request.token(), request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    private static String deviceInfo(final HttpServletRequest request) {
        final var userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return null;
        }
        return userAgent.length() > 255 ? userAgent.substring(0, 255) : userAgent;
    }
}
