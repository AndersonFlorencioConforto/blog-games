package br.com.andersondev.application.auth.refresh;

public record RefreshTokenCommand(String refreshToken, String deviceInfo) {

    public static RefreshTokenCommand with(final String refreshToken, final String deviceInfo) {
        return new RefreshTokenCommand(refreshToken, deviceInfo);
    }
}
