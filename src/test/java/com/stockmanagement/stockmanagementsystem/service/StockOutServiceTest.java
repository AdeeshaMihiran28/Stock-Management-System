package com.stockmanagement.stockmanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.ProductStatus;
import com.stockmanagement.stockmanagementsystem.model.StockOutForm;
import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;
import com.stockmanagement.stockmanagementsystem.repository.ProductRepository;
import com.stockmanagement.stockmanagementsystem.repository.StockTransactionRepository;

@ExtendWith(MockitoExtension.class)
class StockOutServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @InjectMocks
    private StockOutService stockOutService;

    @Test
    void decreasesQuantityAndSavesOutTransactionWithoutSupplier() {
        Product product = activeProduct(30);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        stockOutService.processStockOut(validForm(8));

        assertThat(product.getQuantity()).isEqualTo(22);
        verify(productRepository).save(product);
        ArgumentCaptor<StockTransaction> transactionCaptor = ArgumentCaptor.forClass(StockTransaction.class);
        verify(stockTransactionRepository).save(transactionCaptor.capture());
        StockTransaction transaction = transactionCaptor.getValue();
        assertThat(transaction.getTransactionType()).isEqualTo(StockTransactionType.OUT);
        assertThat(transaction.getSupplier()).isNull();
        assertThat(transaction.getProduct()).isSameAs(product);
        assertThat(transaction.getQuantity()).isEqualTo(8);
    }

    @Test
    void allowsRemovingTheExactAvailableQuantity() {
        Product product = activeProduct(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        stockOutService.processStockOut(validForm(10));

        assertThat(product.getQuantity()).isZero();
        verify(stockTransactionRepository).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsInsufficientStockWithoutSavingAnything() {
        Product product = activeProduct(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> stockOutService.processStockOut(validForm(10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient stock. Available quantity is 5.");

        assertThat(product.getQuantity()).isEqualTo(5);
        verify(productRepository, never()).save(product);
        verify(stockTransactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsInvalidQuantityBeforeLoadingTheProduct() {
        assertThatThrownBy(() -> stockOutService.processStockOut(validForm(0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be at least 1.");

        verify(productRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void rejectsInactiveProductWithoutSavingAnything() {
        Product product = activeProduct(10);
        product.setStatus(ProductStatus.INACTIVE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> stockOutService.processStockOut(validForm(2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This product is inactive.");

        assertThat(product.getQuantity()).isEqualTo(10);
        verify(productRepository, never()).save(product);
        verify(stockTransactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private StockOutForm validForm(int quantity) {
        StockOutForm form = new StockOutForm();
        form.setProductId(1L);
        form.setQuantity(quantity);
        form.setTransactionDate(LocalDateTime.of(2026, 10, 11, 12, 0));
        form.setReferenceNumber(" SO-1001 ");
        form.setNotes(" Customer order ");
        return form;
    }

    private Product activeProduct(int quantity) {
        Product product = new Product();
        product.setId(1L);
        product.setProductCode("P001");
        product.setQuantity(quantity);
        product.setStatus(ProductStatus.ACTIVE);
        return product;
    }
}
