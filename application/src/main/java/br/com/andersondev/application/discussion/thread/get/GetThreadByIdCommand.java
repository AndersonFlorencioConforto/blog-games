package br.com.andersondev.application.discussion.thread.get;

/**
 * Comando para busca de thread por ID.
 */
public record GetThreadByIdCommand(String threadId) {

    public static GetThreadByIdCommand with(final String threadId) {
        return new GetThreadByIdCommand(threadId);
    }
}
