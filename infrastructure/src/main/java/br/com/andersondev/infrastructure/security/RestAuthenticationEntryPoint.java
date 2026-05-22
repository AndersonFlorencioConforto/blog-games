package br.com.andersondev.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

/**
 * Retorna 401 em JSON (RFC 7807 simplificado) quando ha falha de autenticacao.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final var body = Map.of(
                "type", "https://blogames.com/errors/unauthorized",
                "title", "Unauthorized",
                "status", HttpStatus.UNAUTHORIZED.value(),
                "detail", "Autenticacao necessaria ou token invalido",
                "instance", request.getRequestURI(),
                "timestamp", Instant.now().toString()
        );
        this.objectMapper.writeValue(response.getOutputStream(), body);
    }
}
