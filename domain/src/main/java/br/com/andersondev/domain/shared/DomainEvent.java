package br.com.andersondev.domain.shared;

import java.time.Instant;

/**
 * Contrato base para eventos de dominio.
 */
public interface DomainEvent {

    Instant occurredOn();
}
