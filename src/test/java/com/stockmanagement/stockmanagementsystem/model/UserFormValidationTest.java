package com.stockmanagement.stockmanagementsystem.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class UserFormValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsInvalidAccountDetails() {
        UserForm form = new UserForm();
        form.setUsername("a bad username");
        form.setFullName("Test User");
        form.setEmail("not-an-email");
        form.setRole(UserRole.INVENTORY_MANAGER);
        form.setPassword("short");

        Set<String> invalidFields = validator.validate(form).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("username", "email", "password");
    }

    @Test
    void rejectsPasswordLongerThanBcryptSupports() {
        UserForm form = new UserForm();
        form.setUsername("test.user");
        form.setFullName("Test User");
        form.setRole(UserRole.INVENTORY_MANAGER);
        form.setPassword("x".repeat(73));

        Set<String> invalidFields = validator.validate(form).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("password");
    }
}
