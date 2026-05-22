package br.com.andersondev.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserIdTest {

    @Test
    void givenUnique_whenCalled_thenGeneratesNonNullValue() {
        final var id = UserId.unique();

        assertNotNull(id.getValue());
    }

    @Test
    void givenTwoUnique_whenGenerated_thenDiffer() {
        assertNotEquals(UserId.unique().getValue(), UserId.unique().getValue());
    }

    @Test
    void givenValue_whenFrom_thenRehydratesSameValue() {
        final var id = UserId.unique();

        assertEquals(id, UserId.from(id.getValue()));
        assertEquals(id.hashCode(), UserId.from(id.getValue()).hashCode());
    }

    @Test
    void givenDifferentValues_whenEquals_thenNotEqual() {
        assertNotEquals(UserId.from("a"), UserId.from("b"));
    }
}
