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

import com.stockmanagement.stockmanagementsystem.model.Supplier;
import com.stockmanagement.stockmanagementsystem.repository.SupplierRepository;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    @Test
    void rejectsDuplicateSupplierCode() {
        Supplier supplier = supplier(" SUP001 ");
        when(supplierRepository.existsBySupplierCodeIgnoreCase("SUP001")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.saveSupplier(supplier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A supplier with this code already exists.");
        verify(supplierRepository, never()).save(supplier);
    }

    @Test
    void allowsExistingCodeWhenUpdatingTheSameSupplier() {
        Supplier existing = supplier("SUP001");
        existing.setId(1L);
        Supplier changes = supplier(" SUP001 ");
        when(supplierRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(supplierRepository.existsBySupplierCodeIgnoreCaseAndIdNot("SUP001", 1L)).thenReturn(false);

        supplierService.updateSupplier(1L, changes);

        verify(supplierRepository).save(existing);
    }

    @Test
    void searchesWithTrimmedKeyword() {
        supplierService.searchSuppliers(" acme ");

        verify(supplierRepository).search("acme");
    }

    private Supplier supplier(String code) {
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(code);
        supplier.setName("Acme Supplies");
        supplier.setPhone("0112345678");
        return supplier;
    }
}
