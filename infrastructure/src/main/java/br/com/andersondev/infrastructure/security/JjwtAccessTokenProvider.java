package br.com.andersondev.infrastructure.security;

import br.com.andersondev.application.auth.port.AccessToken;
import br.com.andersondev.application.auth.port.AccessTokenClaims;
import br.com.andersondev.application.auth.port.AccessTokenProvider;
import br.com.andersondev.domain.exception.TokenExpiredException;
import br.com.andersondev.domain.shared.IdUtils;
import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Emissao e validacao de access tokens JWT HS256 com JJWT (ADR-0003).
 * Claims: sub (userId), email, role, jti, iat, exp.
 */
@Component
public class JjwtAccessTokenProvider implements AccessTokenProvider {

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JjwtAccessTokenProvider(final JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public AccessToken generate(final UserId userId, final String email, final Role role) {
        final var now = Instant.now();
        final var expiresAt = now.plusSeconds(this.properties.getAccessTokenExpiration());
        final var jti = IdUtils.uuid();

        final var token = Jwts.builder()
                .issuer(this.properties.getIssuer())
                .subject(userId.getValue())
                .id(jti)
                .claim("email", email)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(this.signingKey)
                .compact();

        return new AccessToken(token, jti, expiresAt, this.properties.getAccessTokenExpiration());
    }

    @Override
    public AccessTokenClaims parse(final String token) {
        try {
            final Claims claims = Jwts.parser()
                    .verifyWith(this.signingKey)
                    .requireIssuer(this.properties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new AccessTokenClaims(
                    claims.getSubject(),
                    claims.get("email", String.class),
                    claims.get("role", String.class),
                    claims.getId(),
                    claims.getExpiration().toInstant()
            );
        } catch (final JwtException | IllegalArgumentException ex) {
            throw TokenExpiredException.create();
        }
    }
}
