package br.com.andersondev.domain.user;

import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.user.event.UserRegisteredEvent;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Objects;

/**
 * Raiz do agregado User. Armazena o hash da senha (nunca a senha em texto puro).
 */
public class User extends AggregateRoot<UserId> {

    private String name;
    private final Email email;
    private String passwordHash;
    private Role role;
    private String bio;
    private String avatarUrl;
    private Instant bannedUntil;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(
            final UserId id,
            final String name,
            final Email email,
            final String passwordHash,
            final Role role,
            final String bio,
            final String avatarUrl,
            final Instant bannedUntil,
            final boolean active,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id);
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.bannedUntil = bannedUntil;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Cria um novo usuario. {@code passwordHash} ja deve estar hasheado (BCrypt).
     */
    public static User newUser(
            final String name,
            final String rawEmail,
            final String passwordHash
    ) {
        final var now = Instant.now();
        final var user = new User(
                UserId.unique(),
                name,
                Email.of(rawEmail),
                passwordHash,
                Role.USER,
                null,
                null,
                null,
                true,
                now,
                now
        );
        user.registerEvent(UserRegisteredEvent.of(user.getId().getValue(), user.getEmail().getValue()));
        return user;
    }

    /**
     * Reidrata um usuario existente a partir da persistencia.
     */
    public static User with(
            final UserId id,
            final String name,
            final Email email,
            final String passwordHash,
            final Role role,
            final String bio,
            final String avatarUrl,
            final Instant bannedUntil,
            final boolean active,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new User(
                id, name, email, passwordHash, role, bio, avatarUrl,
                bannedUntil, active, createdAt, updatedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new UserValidator(this, handler).validate();
    }

    public boolean isBanned(final Instant now) {
        return this.bannedUntil != null && now.isBefore(this.bannedUntil);
    }

    public boolean isActive() {
        return this.active;
    }

    public void updateProfile(final String name, final String bio, final String avatarUrl) {
        this.name = name;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }

    public void changePasswordHash(final String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash);
        this.updatedAt = Instant.now();
    }

    public void banUntil(final Instant until) {
        this.bannedUntil = until;
        this.updatedAt = Instant.now();
    }

    public String getName() {
        return this.name;
    }

    public Email getEmail() {
        return this.email;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public Role getRole() {
        return this.role;
    }

    public String getBio() {
        return this.bio;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public Instant getBannedUntil() {
        return this.bannedUntil;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }
}
