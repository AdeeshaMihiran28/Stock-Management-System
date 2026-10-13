package com.stockmanagement.stockmanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmanagement.stockmanagementsystem.model.ProductStatus;
import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;
import com.stockmanagement.stockmanagementsystem.repository.CategoryRepository;
import com.stockmanagement.stockmanagementsystem.repository.ProductRepository;
import com.stockmanagement.stockmanagementsystem.repository.StockTransactionRepository;
import com.stockmanagement.stockmanagementsystem.repository.SupplierRepository;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ReportService reportService;

    @Test
    void readsDashboardMetricsFromExistingRepositories() {
        when(productRepository.count()).thenReturn(25L);
        when(categoryRepository.count()).thenReturn(6L);
        when(supplierRepository.count()).thenReturn(8L);
        when(productRepository.sumCurrentStockQuantity()).thenReturn(430L);
        when(productService.getLowStockCount()).thenReturn(4L);
        when(productRepository.countByStatusAndQuantity(ProductStatus.ACTIVE, 0)).thenReturn(2L);

        assertThat(reportService.getTotalProducts()).isEqualTo(25);
        assertThat(reportService.getTotalCategories()).isEqualTo(6);
        assertThat(reportService.getTotalSuppliers()).isEqualTo(8);
        assertThat(reportService.getTotalStockQuantity()).isEqualTo(430);
        assertThat(reportService.getLowStockCount()).isEqualTo(4);
        assertThat(reportService.getOutOfStockCount()).isEqualTo(2);
    }

    @Test
    void convertsInclusiveReportDatesToDateTimeRange() {
        LocalDate fromDate = LocalDate.of(2026, 10, 1);
        LocalDate toDate = LocalDate.of(2026, 10, 31);

        reportService.getTransactionReport(StockTransactionType.IN, fromDate, toDate);

        verify(stockTransactionRepository).findForReport(
                StockTransactionType.IN, fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay());
    }

    @Test
    void rejectsReversedDateRange() {
        LocalDate fromDate = LocalDate.of(2026, 10, 31);
        LocalDate toDate = LocalDate.of(2026, 10, 1);

        assertThatThrownBy(() -> reportService.getTransactionReport(null, fromDate, toDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("From date must not be after To date.");
        verify(stockTransactionRepository, never()).findForReport(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }
}
