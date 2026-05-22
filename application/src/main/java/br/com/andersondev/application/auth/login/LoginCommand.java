package br.com.andersondev.application.auth.login;

public record LoginCommand(String email, String password, String deviceInfo) {

    public static LoginCommand with(final String email, final String password, final String deviceInfo) {
        return new LoginCommand(email, password, deviceInfo);
    }
}
