package br.com.andersondev.domain.user;

import br.com.andersondev.domain.validation.Error;
import br.com.andersondev.domain.validation.ValidationHandler;
import br.com.andersondev.domain.validation.Validator;

/**
 * Valida invariantes da entidade User (nome, bio). Email/role sao garantidos
 * pelos value objects. Politica de senha e validada na criacao via PasswordPolicy.
 */
public class UserValidator extends Validator {

    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 100;
    private static final int BIO_MAX_LENGTH = 500;
    private static final int AVATAR_URL_MAX_LENGTH = 500;

    private final User user;

    public UserValidator(final User user, final ValidationHandler handler) {
        super(handler);
        this.user = user;
    }

    @Override
    public void validate() {
        checkNameConstraints();
        checkBioConstraints();
        checkAvatarUrlConstraints();
    }

    private void checkNameConstraints() {
        final var name = this.user.getName();
        if (name == null || name.isBlank()) {
            validationHandler().append(new Error("'name' e obrigatorio"));
            return;
        }
        final var length = name.trim().length();
        if (length < NAME_MIN_LENGTH || length > NAME_MAX_LENGTH) {
            validationHandler().append(new Error("'name' deve ter entre 2 e 100 caracteres"));
        }
    }

    private void checkBioConstraints() {
        final var bio = this.user.getBio();
        if (bio != null && bio.length() > BIO_MAX_LENGTH) {
            validationHandler().append(new Error("'bio' deve ter no maximo 500 caracteres"));
        }
    }

    private void checkAvatarUrlConstraints() {
        final var avatarUrl = this.user.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return;
        }
        if (avatarUrl.length() > AVATAR_URL_MAX_LENGTH) {
            validationHandler().append(new Error("'avatarUrl' deve ter no maximo 500 caracteres"));
            return;
        }
        if (!isValidUrl(avatarUrl)) {
            validationHandler().append(new Error("'avatarUrl' deve ser uma URL valida"));
        }
    }

    private static boolean isValidUrl(final String value) {
        try {
            final var uri = java.net.URI.create(value.trim());
            final var scheme = uri.getScheme();
            return uri.isAbsolute()
                    && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null && !uri.getHost().isBlank();
        } catch (final IllegalArgumentException ex) {
            return false;
        }
    }
}
