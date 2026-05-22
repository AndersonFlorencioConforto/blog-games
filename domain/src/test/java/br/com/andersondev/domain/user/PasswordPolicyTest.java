package br.com.andersondev.domain.user;

import br.com.andersondev.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordPolicyTest {

    @Test
    void givenStrongPassword_whenValidate_thenPasses() {
        assertDoesNotThrow(() -> PasswordPolicy.validate("MinhaSenh@123"));
    }

    @Test
    void givenNull_whenValidate_thenThrows() {
        assertThrows(DomainException.class, () -> PasswordPolicy.validate(null));
    }

    @Test
    void givenBlank_whenValidate_thenThrows() {
        assertThrows(DomainException.class, () -> PasswordPolicy.validate("   "));
    }

    @Test
    void givenShortPassword_whenValidate_thenThrows() {
        assertThrows(DomainException.class, () -> PasswordPolicy.validate("Ab@1"));
    }

    @Test
    void givenNoUpperCase_whenValidate_thenThrows() {
        assertThrows(DomainException.class, () -> PasswordPolicy.validate("minhasenh@123"));
    }

    @Test
    void givenNoLowerCase_whenValidate_thenThrows() {
        assertThrows(DomainException.class, () -> PasswordPolicy.validate("MINHASENH@123"));
    }

    @Test
    void givenNoDigit_whenValidate_thenThrows() {
        assertThrows(DomainException.class, () -> PasswordPolicy.validate("MinhaSenha@"));
    }

    @Test
    void givenNoSpecialChar_whenValidate_thenThrows() {
        assertThrows(DomainException.class, () -> PasswordPolicy.validate("MinhaSenha123"));
    }
}
