package com.stockmanagement.stockmanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.ProductStatus;
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

    @Test
    void startsNewProductsWithZeroQuantity() {
        Product product = new Product();
        product.setProductCode("P001");
        product.setName("Wireless Mouse");
        product.setQuantity(50);

        productService.saveProduct(product);

        assertThat(product.getQuantity()).isZero();
        verify(productRepository).save(product);
    }

    @Test
    void preservesQuantityWhenProductDetailsAreUpdated() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setProductCode("P001");
        existing.setName("Mouse");
        existing.setQuantity(25);
        Product changes = new Product();
        changes.setProductCode("P001");
        changes.setName("Wireless Mouse");
        changes.setQuantity(999);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        productService.updateProduct(1L, changes);

        assertThat(existing.getQuantity()).isEqualTo(25);
        verify(productRepository).save(existing);
    }

    @Test
    void loadsAndCountsOnlyActiveLowStockProducts() {
        productService.getLowStockProducts();
        productService.getLowStockCount();

        verify(productRepository).findLowStockProducts(ProductStatus.ACTIVE);
        verify(productRepository).countLowStockProducts(ProductStatus.ACTIVE);
    }
}
