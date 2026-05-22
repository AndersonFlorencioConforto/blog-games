package br.com.andersondev.application;

/**
 * Contrato base de caso de uso com entrada e saida.
 *
 * @param <I> tipo de entrada (command)
 * @param <O> tipo de saida (output)
 */
public abstract class UseCase<I, O> {

    public abstract O execute(I input);
}
