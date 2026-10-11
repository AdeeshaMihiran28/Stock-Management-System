package com.stockmanagement.stockmanagementsystem.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class StockInFormValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsMissingSelectionsDateAndNonPositiveQuantity() {
        StockInForm form = new StockInForm();
        form.setQuantity(0);

        Set<String> invalidFields = validator.validate(form).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("productId", "supplierId", "quantity", "transactionDate");
    }
}
