package br.com.andersondev.application.auth.password;

public record ResetPasswordCommand(String token, String newPassword) {

    public static ResetPasswordCommand with(final String token, final String newPassword) {
        return new ResetPasswordCommand(token, newPassword);
    }
}
