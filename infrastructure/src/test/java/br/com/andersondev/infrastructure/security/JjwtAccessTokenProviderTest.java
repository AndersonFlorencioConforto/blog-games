package br.com.andersondev.infrastructure.security;

import br.com.andersondev.domain.exception.TokenExpiredException;
import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.infrastructure.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JjwtAccessTokenProviderTest {

    private JjwtAccessTokenProvider provider;

    @BeforeEach
    void setUp() {
        final var props = new JwtProperties();
        props.setSecret("unit-test-secret-key-with-at-least-32-bytes-long");
        props.setAccessTokenExpiration(900);
        props.setIssuer("blog-games-platform");
        provider = new JjwtAccessTokenProvider(props);
    }

    @Test
    void givenUser_whenGenerateAndParse_thenRoundTripsClaims() {
        final var userId = UserId.unique();

        final var generated = provider.generate(userId, "anderson@email.com", Role.ADMIN);
        assertNotNull(generated.token());
        assertNotNull(generated.jti());
        assertEquals(900, generated.expiresInSeconds());

        final var claims = provider.parse(generated.token());
        assertEquals(userId.getValue(), claims.userId());
        assertEquals("anderson@email.com", claims.email());
        assertEquals("ADMIN", claims.role());
        assertEquals(generated.jti(), claims.jti());
    }

    @Test
    void givenTamperedToken_whenParse_thenThrowsTokenExpired() {
        final var generated = provider.generate(UserId.unique(), "a@b.com", Role.USER);
        final var tampered = generated.token().substring(0, generated.token().length() - 2) + "xy";

        assertThrows(TokenExpiredException.class, () -> provider.parse(tampered));
    }

    @Test
    void givenGarbageToken_whenParse_thenThrowsTokenExpired() {
        assertThrows(TokenExpiredException.class, () -> provider.parse("not.a.jwt"));
    }

    @Test
    void givenTokenSignedWithOtherSecret_whenParse_thenThrowsTokenExpired() {
        final var otherProps = new JwtProperties();
        otherProps.setSecret("a-completely-different-secret-key-32-bytes-long!!");
        otherProps.setIssuer("blog-games-platform");
        final var otherProvider = new JjwtAccessTokenProvider(otherProps);

        final var foreign = otherProvider.generate(UserId.unique(), "a@b.com", Role.USER);

        assertThrows(TokenExpiredException.class, () -> provider.parse(foreign.token()));
    }
}
