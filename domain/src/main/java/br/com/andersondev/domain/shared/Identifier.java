package br.com.andersondev.domain.shared;

/**
 * Contrato comum para identificadores de agregados.
 */
public abstract class Identifier extends ValueObject {

    public abstract String getValue();
}
