package br.com.andersondev.domain.shared;

import br.com.andersondev.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base de entidades: identidade por ID, igualdade por ID e ciclo de eventos de dominio.
 *
 * @param <ID> tipo do identificador da entidade
 */
public abstract class Entity<ID extends Identifier> {

    protected final ID id;

    private final List<DomainEvent> domainEvents;

    protected Entity(final ID id) {
        this.id = Objects.requireNonNull(id, "'id' nao pode ser nulo");
        this.domainEvents = new ArrayList<>();
    }

    protected Entity(final ID id, final List<DomainEvent> domainEvents) {
        this.id = Objects.requireNonNull(id, "'id' nao pode ser nulo");
        this.domainEvents = new ArrayList<>(domainEvents == null ? List.of() : domainEvents);
    }

    public abstract void validate(ValidationHandler handler);

    public ID getId() {
        return this.id;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    protected void registerEvent(final DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    public void clearEvents() {
        this.domainEvents.clear();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Entity<?> entity = (Entity<?>) o;
        return Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
