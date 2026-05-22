package br.com.andersondev.domain.exception;

import br.com.andersondev.domain.shared.Identifier;
import br.com.andersondev.domain.validation.Error;

import java.io.Serial;
import java.util.List;

/**
 * Entidade nao encontrada. Mapeada para 404.
 */
public class EntityNotFoundException extends DomainException {

    @Serial
    private static final long serialVersionUID = 1L;

    private EntityNotFoundException(final String message) {
        super(message, List.of(new Error(message)));
    }

    public static EntityNotFoundException with(final Class<?> aggregate, final Identifier id) {
        final var message = "%s com ID %s nao foi encontrado".formatted(
                aggregate.getSimpleName(), id.getValue());
        return new EntityNotFoundException(message);
    }

    public static EntityNotFoundException with(final String message) {
        return new EntityNotFoundException(message);
    }
}
