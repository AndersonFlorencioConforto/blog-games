package br.com.andersondev.infrastructure.api;

import br.com.andersondev.domain.exception.BusinessRuleViolationException;
import br.com.andersondev.domain.exception.DomainException;
import br.com.andersondev.domain.exception.DuplicateEntityException;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.exception.ForbiddenOperationException;
import br.com.andersondev.domain.exception.InvalidCredentialsException;
import br.com.andersondev.domain.exception.InvalidRangeException;
import br.com.andersondev.domain.exception.SelfReferenceException;
import br.com.andersondev.domain.exception.TokenAlreadyUsedException;
import br.com.andersondev.domain.exception.TokenExpiredException;
import br.com.andersondev.domain.exception.UserBannedException;
import br.com.andersondev.domain.validation.Error;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Mapeia excecoes de dominio para os status HTTP do api-catalog (RFC 7807 simplificado).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            final EntityNotFoundException ex, final HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Not Found", ex, request);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(
            final DuplicateEntityException ex, final HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, "Conflict", ex, request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            final InvalidCredentialsException ex, final HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", ex, request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpired(
            final TokenExpiredException ex, final HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", ex, request);
    }

    @ExceptionHandler(UserBannedException.class)
    public ResponseEntity<Map<String, Object>> handleBanned(
            final UserBannedException ex, final HttpServletRequest request) {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", ex, request);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(
            final ForbiddenOperationException ex, final HttpServletRequest request) {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", ex, request);
    }

    @ExceptionHandler({TokenAlreadyUsedException.class, SelfReferenceException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            final DomainException ex, final HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Bad Request", ex, request);
    }

    @ExceptionHandler({InvalidRangeException.class, BusinessRuleViolationException.class})
    public ResponseEntity<Map<String, Object>> handleUnprocessableSpecific(
            final DomainException ex, final HttpServletRequest request) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex, request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomain(
            final DomainException ex, final HttpServletRequest request) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        final var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Error(fe.getDefaultMessage()))
                .toList();
        final var detail = errors.isEmpty() ? "Dados invalidos" : errors.getFirst().message();
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(body(HttpStatus.UNPROCESSABLE_ENTITY, "Validation Error", detail, errors, request));
    }

    private ResponseEntity<Map<String, Object>> problem(
            final HttpStatus status,
            final String title,
            final DomainException ex,
            final HttpServletRequest request
    ) {
        return ResponseEntity.status(status)
                .body(body(status, title, ex.getMessage(), ex.getErrors(), request));
    }

    private Map<String, Object> body(
            final HttpStatus status,
            final String title,
            final String detail,
            final List<Error> errors,
            final HttpServletRequest request
    ) {
        return Map.of(
                "type", "https://blogames.com/errors/" + title.toLowerCase().replace(' ', '-'),
                "title", title,
                "status", status.value(),
                "detail", detail == null ? title : detail,
                "instance", request.getRequestURI(),
                "timestamp", Instant.now().toString(),
                "errors", errors == null ? List.of() : errors.stream().map(Error::message).toList()
        );
    }
}
