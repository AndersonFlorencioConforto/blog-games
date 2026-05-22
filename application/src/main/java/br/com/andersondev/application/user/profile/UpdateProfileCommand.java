package br.com.andersondev.application.user.profile;

/**
 * Comando para atualizar o perfil do usuario autenticado (PUT /users/me).
 * {@code userId} vem do contexto autenticado (nunca do path/body), garantindo
 * que cada usuario so edita o proprio perfil (PR-04).
 */
public record UpdateProfileCommand(String userId, String name, String bio, String avatarUrl) {

    public static UpdateProfileCommand with(
            final String userId,
            final String name,
            final String bio,
            final String avatarUrl
    ) {
        return new UpdateProfileCommand(userId, name, bio, avatarUrl);
    }
}
