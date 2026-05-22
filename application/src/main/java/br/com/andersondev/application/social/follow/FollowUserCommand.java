package br.com.andersondev.application.social.follow;

/**
 * Comando para seguir/deixar de seguir um usuario.
 * {@code followerId} vem sempre do contexto autenticado (nunca do path/body).
 * {@code followedId} e o alvo (path /users/{id}/follow).
 */
public record FollowUserCommand(String followerId, String followedId) {

    public static FollowUserCommand with(final String followerId, final String followedId) {
        return new FollowUserCommand(followerId, followedId);
    }
}
