package com.stockmanagement.stockmanagementsystem.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class SupplierValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsMissingRequiredFieldsAndInvalidEmail() {
        Supplier supplier = new Supplier();
        supplier.setEmail("not-an-email");

        Set<String> invalidFields = validator.validate(supplier).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("supplierCode", "name", "phone", "email", "status");
    }

    @Test
    void rejectsInvalidCodeAndPhone() {
        Supplier supplier = new Supplier();
        supplier.setSupplierCode("SUP 01!");
        supplier.setName("Supplier");
        supplier.setPhone("phone-number");

        Set<String> invalidFields = validator.validate(supplier).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("supplierCode", "phone");
    }
}
