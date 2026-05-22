package br.com.andersondev.application;

/**
 * Caso de uso sem retorno (void).
 *
 * @param <I> tipo de entrada (command)
 */
public abstract class UnitUseCase<I> {

    public abstract void execute(I input);
}
