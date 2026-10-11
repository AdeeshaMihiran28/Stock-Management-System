package com.stockmanagement.stockmanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void rejectsDuplicateProductCode() {
        Product product = new Product();
        product.setProductCode(" P001 ");
        product.setName("Wireless Mouse");
        when(productRepository.existsByProductCodeIgnoreCase("P001")).thenReturn(true);

        assertThatThrownBy(() -> productService.saveProduct(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A product with this code already exists.");
        verify(productRepository, never()).save(product);
    }

    @Test
    void searchesNameAndCodeWithTrimmedKeyword() {
        productService.searchProducts(" laptop ");

        verify(productRepository)
                .findByNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrderByNameAsc("laptop", "laptop");
    }
}
