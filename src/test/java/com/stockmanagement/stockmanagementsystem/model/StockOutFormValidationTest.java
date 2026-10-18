package com.stockmanagement.stockmanagementsystem.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class StockOutFormValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsMissingProductDateAndNonPositiveQuantity() {
        StockOutForm form = new StockOutForm();
        form.setQuantity(-5);

        Set<String> invalidFields = validator.validate(form).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("productId", "quantity", "transactionDate");
    }

    @Test
    void rejectsInvalidProductIdAndFutureDate() {
        StockOutForm form = new StockOutForm();
        form.setProductId(0L);
        form.setQuantity(1);
        form.setTransactionDate(LocalDateTime.now().plusDays(1));

        Set<String> invalidFields = validator.validate(form).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("productId", "transactionDate");
    }
}
