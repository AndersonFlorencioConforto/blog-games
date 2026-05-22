package br.com.andersondev.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

/**
 * Retorna 403 em JSON quando o usuario autenticado nao tem permissao (role).
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final var body = Map.of(
                "type", "https://blogames.com/errors/forbidden",
                "title", "Forbidden",
                "status", HttpStatus.FORBIDDEN.value(),
                "detail", "Acesso negado para o recurso solicitado",
                "instance", request.getRequestURI(),
                "timestamp", Instant.now().toString()
        );
        this.objectMapper.writeValue(response.getOutputStream(), body);
    }
}
