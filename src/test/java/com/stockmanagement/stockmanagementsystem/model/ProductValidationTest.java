package com.stockmanagement.stockmanagementsystem.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ProductValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsNegativeValuesAndMissingCategory() {
        Product product = new Product();
        product.setProductCode("P001");
        product.setName("Wireless Mouse");
        product.setQuantity(-1);
        product.setReorderLevel(-1);
        product.setUnitPrice(new BigDecimal("-0.01"));
        product.setCategory(null);

        Set<String> invalidFields = validator.validate(product).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("quantity", "reorderLevel", "unitPrice", "category");
    }

    @Test
    void rejectsInvalidCodeAndPricePrecision() {
        Product product = new Product();
        product.setProductCode("P 001!");
        product.setName("Wireless Mouse");
        product.setQuantity(0);
        product.setReorderLevel(0);
        product.setUnitPrice(new BigDecimal("12.345"));

        Set<String> invalidFields = validator.validate(product).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains("productCode", "unitPrice");
    }
}
