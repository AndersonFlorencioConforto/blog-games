package br.com.andersondev.application.social.follow;

/**
 * Comando de listagem paginada social (seguidores/seguindo de um usuario).
 */
public record ListSocialCommand(String userId, int page, int size) {

    public static ListSocialCommand with(final String userId, final int page, final int size) {
        return new ListSocialCommand(userId, page, size);
    }
}
