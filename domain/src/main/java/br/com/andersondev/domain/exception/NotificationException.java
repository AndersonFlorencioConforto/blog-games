package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.validation.handler.Notification;

import java.io.Serial;

/**
 * Excecao que agrega multiplos erros de validacao coletados em um {@link Notification}.
 * Mapeada para 422 (Unprocessable Entity).
 */
public class NotificationException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotificationException(final String message, final Notification notification) {
        super(message, notification.getErrors());
    }
}
