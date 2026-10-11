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
import com.stockmanagement.stockmanagementsystem.model.StockInForm;
import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;
import com.stockmanagement.stockmanagementsystem.model.Supplier;
import com.stockmanagement.stockmanagementsystem.model.SupplierStatus;
import com.stockmanagement.stockmanagementsystem.repository.ProductRepository;
import com.stockmanagement.stockmanagementsystem.repository.StockTransactionRepository;
import com.stockmanagement.stockmanagementsystem.repository.SupplierRepository;

@ExtendWith(MockitoExtension.class)
class StockInServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @InjectMocks
    private StockInService stockInService;

    @Test
    void increasesProductQuantityAndSavesStockInHistory() {
        Product product = activeProduct(20);
        Supplier supplier = activeSupplier();
        StockInForm form = validForm(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));

        stockInService.processStockIn(form);

        assertThat(product.getQuantity()).isEqualTo(30);
        verify(productRepository).save(product);
        ArgumentCaptor<StockTransaction> transactionCaptor = ArgumentCaptor.forClass(StockTransaction.class);
        verify(stockTransactionRepository).save(transactionCaptor.capture());
        StockTransaction transaction = transactionCaptor.getValue();
        assertThat(transaction.getTransactionType()).isEqualTo(StockTransactionType.IN);
        assertThat(transaction.getProduct()).isSameAs(product);
        assertThat(transaction.getSupplier()).isSameAs(supplier);
        assertThat(transaction.getQuantity()).isEqualTo(10);
    }

    @Test
    void rejectsInactiveProductWithoutChangingQuantity() {
        Product product = activeProduct(20);
        product.setStatus(ProductStatus.INACTIVE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(activeSupplier()));

        assertThatThrownBy(() -> stockInService.processStockIn(validForm(10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stock cannot be added to an inactive product.");

        assertThat(product.getQuantity()).isEqualTo(20);
        verify(productRepository, never()).save(product);
        verify(stockTransactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsZeroQuantityBeforeLoadingRecords() {
        assertThatThrownBy(() -> stockInService.processStockIn(validForm(0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be at least 1.");

        verify(productRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void rejectsInactiveSupplierWithoutChangingQuantity() {
        Product product = activeProduct(20);
        Supplier supplier = activeSupplier();
        supplier.setStatus(SupplierStatus.INACTIVE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));

        assertThatThrownBy(() -> stockInService.processStockIn(validForm(10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("An inactive supplier cannot be used for Stock-In.");

        assertThat(product.getQuantity()).isEqualTo(20);
        verify(productRepository, never()).save(product);
        verify(stockTransactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private StockInForm validForm(int quantity) {
        StockInForm form = new StockInForm();
        form.setProductId(1L);
        form.setSupplierId(2L);
        form.setQuantity(quantity);
        form.setTransactionDate(LocalDateTime.of(2026, 10, 11, 12, 0));
        form.setReferenceNumber(" PO-1001 ");
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

    private Supplier activeSupplier() {
        Supplier supplier = new Supplier();
        supplier.setId(2L);
        supplier.setSupplierCode("SUP001");
        supplier.setStatus(SupplierStatus.ACTIVE);
        return supplier;
    }
}
