package br.com.andersondev.infrastructure.config;

import br.com.andersondev.infrastructure.security.JwtAuthenticationFilter;
import br.com.andersondev.infrastructure.security.RestAccessDeniedHandler;
import br.com.andersondev.infrastructure.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracao de seguranca (Spring Security 7 / security-matrix).
 * API stateless, JWT via filtro customizado, CSRF desabilitado (API non-browser),
 * rotas publicas vs protegidas conforme a matriz.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String API = "/api/v1";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            final JwtAuthenticationFilter jwtAuthenticationFilter,
            final RestAuthenticationEntryPoint authenticationEntryPoint,
            final RestAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Actuator basico
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // H2 console (apenas local; protegido por profile)
                        .requestMatchers("/h2-console/**").permitAll()
                        // Autenticacao publica
                        .requestMatchers(HttpMethod.POST,
                                API + "/auth/register",
                                API + "/auth/login",
                                API + "/auth/refresh",
                                API + "/auth/forgot-password",
                                API + "/auth/reset-password").permitAll()
                        // Logout exige autenticacao
                        .requestMatchers(HttpMethod.POST, API + "/auth/logout").authenticated()
                        // Avaliar jogo e ver propria avaliacao exigem autenticacao (security-matrix).
                        // Declarados ANTES do GET publico de /games/** para terem precedencia.
                        .requestMatchers(HttpMethod.POST, API + "/games/*/ratings").authenticated()
                        .requestMatchers(HttpMethod.GET, API + "/games/*/ratings/me").authenticated()
                        // Perfil proprio exige autenticacao. Declarado ANTES do GET publico /users/*
                        // (que casaria com /users/me) para ter precedencia (security-matrix).
                        .requestMatchers(HttpMethod.GET, API + "/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, API + "/users/me").authenticated()
                        // Seguir/deixar de seguir exige autenticacao (follower = principal).
                        .requestMatchers(HttpMethod.POST, API + "/users/*/follow").authenticated()
                        .requestMatchers(HttpMethod.DELETE, API + "/users/*/follow").authenticated()
                        // Escrita na propria estante exige autenticacao (dono = principal).
                        .requestMatchers(HttpMethod.POST, API + "/shelf").authenticated()
                        .requestMatchers(HttpMethod.PUT, API + "/shelf/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, API + "/shelf/*").authenticated()
                        // Leitura publica (catalogo, perfis, threads, noticias, estante)
                        .requestMatchers(HttpMethod.GET,
                                API + "/games/**",
                                API + "/users/*",
                                API + "/users/*/followers",
                                API + "/users/*/following",
                                API + "/users/*/shelf",
                                API + "/news/**").permitAll()
                        // Writes de jogo: ADMIN (reforcado por @PreAuthorize nos handlers). G-01.
                        .requestMatchers(HttpMethod.POST, API + "/games").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, API + "/games/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, API + "/games/*").hasRole("ADMIN")
                        // Demais rotas exigem autenticacao; autorizacao fina via @PreAuthorize
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(this.authenticationEntryPoint)
                        .accessDeniedHandler(this.accessDeniedHandler))
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // H2 console usa frames
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
