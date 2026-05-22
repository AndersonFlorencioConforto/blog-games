package br.com.andersondev.application.user.register;

/**
 * Comando de cadastro de usuario. Senha em texto puro (validada e hasheada no caso de uso).
 */
public record RegisterUserCommand(String name, String email, String password) {

    public static RegisterUserCommand with(final String name, final String email, final String password) {
        return new RegisterUserCommand(name, email, password);
    }
}
