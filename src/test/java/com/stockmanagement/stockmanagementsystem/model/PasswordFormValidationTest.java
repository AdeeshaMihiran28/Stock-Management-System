package com.stockmanagement.stockmanagementsystem.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class PasswordFormValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsOnlyPasswordsBetweenEightAndSeventyTwoCharacters() {
        PasswordForm shortPassword = new PasswordForm();
        shortPassword.setPassword("short");
        PasswordForm longPassword = new PasswordForm();
        longPassword.setPassword("x".repeat(73));
        PasswordForm validPassword = new PasswordForm();
        validPassword.setPassword("valid-password");

        assertThat(validator.validate(shortPassword)).isNotEmpty();
        assertThat(validator.validate(longPassword)).isNotEmpty();
        assertThat(validator.validate(validPassword)).isEmpty();
    }
}
