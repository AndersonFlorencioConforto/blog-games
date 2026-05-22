package br.com.andersondev.infrastructure.user.persistence;

import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity(name = "User")
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "banned_until")
    private Instant bannedUntil;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UserJpaEntity() {
    }

    public static UserJpaEntity from(final User user) {
        final var entity = new UserJpaEntity();
        entity.id = user.getId().getValue();
        entity.name = user.getName();
        entity.email = user.getEmail().getValue();
        entity.passwordHash = user.getPasswordHash();
        entity.role = user.getRole();
        entity.bio = user.getBio();
        entity.avatarUrl = user.getAvatarUrl();
        entity.bannedUntil = user.getBannedUntil();
        entity.active = user.isActive();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        return entity;
    }

    public User toAggregate() {
        return User.with(
                UserId.from(this.id),
                this.name,
                Email.of(this.email),
                this.passwordHash,
                this.role,
                this.bio,
                this.avatarUrl,
                this.bannedUntil,
                this.active,
                this.createdAt,
                this.updatedAt
        );
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
