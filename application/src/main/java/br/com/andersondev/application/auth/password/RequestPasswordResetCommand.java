package br.com.andersondev.application.auth.password;

public record RequestPasswordResetCommand(String email) {

    public static RequestPasswordResetCommand with(final String email) {
        return new RequestPasswordResetCommand(email);
    }
}
