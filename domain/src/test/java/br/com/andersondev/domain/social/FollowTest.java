package br.com.andersondev.domain.social;

import br.com.andersondev.domain.exception.SelfReferenceException;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FollowTest {

    @Test
    void givenDistinctUsers_whenNewFollow_thenCreates() {
        final var follower = UserId.unique();
        final var followed = UserId.unique();

        final var follow = Follow.newFollow(follower, followed);

        assertEquals(follower, follow.getFollowerId());
        assertEquals(followed, follow.getFollowedId());
        assertNotNull(follow.getFollowedAt());
        assertEquals(FollowId.of(follower, followed), follow.getId());
    }

    @Test
    void givenSameUser_whenNewFollow_thenThrowsSelfReference() {
        final var user = UserId.unique();
        assertThrows(SelfReferenceException.class, () -> Follow.newFollow(user, user));
    }

    @Test
    void givenSamePair_whenEquals_thenFollowIdIsEqual() {
        final var follower = UserId.from("a");
        final var followed = UserId.from("b");
        assertEquals(FollowId.of(follower, followed), FollowId.of(follower, followed));
    }
}
