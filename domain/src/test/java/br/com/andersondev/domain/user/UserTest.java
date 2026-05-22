package br.com.andersondev.domain.user;

import br.com.andersondev.domain.user.event.UserRegisteredEvent;
import br.com.andersondev.domain.validation.handler.Notification;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void givenValidData_whenNewUser_thenCreatesActiveUserWithEvent() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");

        assertEquals("Anderson", user.getName());
        assertEquals("anderson@email.com", user.getEmail().getValue());
        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isActive());
        assertEquals(1, user.getDomainEvents().size());
        assertInstanceOf(UserRegisteredEvent.class, user.getDomainEvents().getFirst());
    }

    @Test
    void givenValidUser_whenValidate_thenNoErrors() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");
        final var notification = Notification.create();

        user.validate(notification);

        assertFalse(notification.hasError());
    }

    @Test
    void givenBlankName_whenValidate_thenHasError() {
        final var user = User.newUser(" ", "anderson@email.com", "hash");
        final var notification = Notification.create();

        user.validate(notification);

        assertTrue(notification.hasError());
    }

    @Test
    void givenTooLongName_whenValidate_thenHasError() {
        final var user = User.newUser("a".repeat(101), "anderson@email.com", "hash");
        final var notification = Notification.create();

        user.validate(notification);

        assertTrue(notification.hasError());
    }

    @Test
    void givenBannedFuture_whenIsBanned_thenTrue() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");
        user.banUntil(Instant.now().plusSeconds(3600));

        assertTrue(user.isBanned(Instant.now()));
    }

    @Test
    void givenBanInPast_whenIsBanned_thenFalse() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");
        user.banUntil(Instant.now().minusSeconds(3600));

        assertFalse(user.isBanned(Instant.now()));
    }

    @Test
    void givenNoBan_whenIsBanned_thenFalse() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");

        assertFalse(user.isBanned(Instant.now()));
    }

    @Test
    void givenUser_whenChangePasswordHash_thenUpdatesHash() {
        final var user = User.newUser("Anderson", "anderson@email.com", "old-hash");

        user.changePasswordHash("new-hash");

        assertEquals("new-hash", user.getPasswordHash());
    }

    @Test
    void givenUser_whenUpdateProfile_thenUpdatesFields() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");

        user.updateProfile("Novo Nome", "minha bio", "http://avatar");

        assertEquals("Novo Nome", user.getName());
        assertEquals("minha bio", user.getBio());
        assertEquals("http://avatar", user.getAvatarUrl());
    }

    @Test
    void givenValidAvatarUrl_whenValidate_thenNoError() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");
        user.updateProfile("Anderson", null, "https://cdn.example.com/avatars/u.jpg");
        final var notification = Notification.create();

        user.validate(notification);

        assertFalse(notification.hasError());
    }

    @Test
    void givenInvalidAvatarUrl_whenValidate_thenHasError() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");
        user.updateProfile("Anderson", null, "not-a-valid-url");
        final var notification = Notification.create();

        user.validate(notification);

        assertTrue(notification.hasError());
    }

    @Test
    void givenTooLongBio_whenValidate_thenHasError() {
        final var user = User.newUser("Anderson", "anderson@email.com", "hash");
        user.updateProfile("Anderson", "b".repeat(501), null);
        final var notification = Notification.create();

        user.validate(notification);

        assertTrue(notification.hasError());
    }
}
