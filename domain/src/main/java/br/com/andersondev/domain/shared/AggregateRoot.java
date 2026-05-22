package br.com.andersondev.domain.shared;

import java.util.List;

/**
 * Raiz de agregado. Ponto de entrada para invariantes e consistencia transacional.
 *
 * @param <ID> tipo do identificador do agregado
 */
public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    protected AggregateRoot(final ID id) {
        super(id);
    }

    protected AggregateRoot(final ID id, final List<DomainEvent> domainEvents) {
        super(id, domainEvents);
    }
}
