package br.com.andersondev.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracao de seguranca de JWT/tokens (ADR-0003).
 */
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    /**
     * Chave simetrica HS256 (minimo 32 bytes). Em local tem default; em prod via JWT_SECRET.
     */
    private String secret = "local-dev-secret-change-me-please-32bytes-minimum!!";

    /** Expiracao do access token em segundos (default 15min). */
    private long accessTokenExpiration = 900;

    /** Expiracao do refresh token em segundos (default 7 dias). */
    private long refreshTokenExpiration = 604800;

    /** Issuer do token. */
    private String issuer = "blog-games-platform";

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(final long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(final long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }
}
