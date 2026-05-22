package br.com.andersondev.infrastructure.security;

import br.com.andersondev.application.auth.port.AccessTokenProvider;
import br.com.andersondev.domain.auth.port.TokenBlocklistGateway;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * Filtro de autenticacao JWT (security-matrix secao 3 / A-04..A-06, T-08).
 * Ordem de verificacao:
 *  1. Token presente no header Authorization Bearer.
 *  2. Assinatura e expiracao validas (delegado ao AccessTokenProvider.parse).
 *  3. jti nao presente na denylist.
 *  4. Usuario existe e active = true.
 *  5. Usuario nao banido.
 * Em qualquer falha, nao popula o SecurityContext (resultando em 401/403 downstream).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AccessTokenProvider accessTokenProvider;
    private final TokenBlocklistGateway tokenBlocklistGateway;
    private final UserGateway userGateway;

    public JwtAuthenticationFilter(
            final AccessTokenProvider accessTokenProvider,
            final TokenBlocklistGateway tokenBlocklistGateway,
            final UserGateway userGateway
    ) {
        this.accessTokenProvider = accessTokenProvider;
        this.tokenBlocklistGateway = tokenBlocklistGateway;
        this.userGateway = userGateway;
    }

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {

        final var header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final var token = header.substring(BEARER_PREFIX.length()).trim();

        if (authenticate(token, request)) {
            // sucesso: contexto populado
            filterChain.doFilter(request, response);
        } else {
            // token presente porem invalido: limpa contexto, segue para entry point (401/403)
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }

    private boolean authenticate(final String token, final HttpServletRequest request) {
        try {
            final var claims = this.accessTokenProvider.parse(token);

            if (this.tokenBlocklistGateway.existsByJti(claims.jti())) {
                return false;
            }

            final var user = this.userGateway.findById(UserId.from(claims.userId())).orElse(null);
            if (user == null || !user.isActive() || user.isBanned(Instant.now())) {
                return false;
            }

            final var principal = new AuthenticatedUser(
                    claims.userId(), claims.email(), claims.role(),
                    claims.jti(), claims.expiresAt());

            final var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + claims.role()));
            final var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (final RuntimeException ex) {
            return false;
        }
    }
}
