package com.stockmanagement.stockmanagementsystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.ProductStatus;
import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;
import com.stockmanagement.stockmanagementsystem.repository.CategoryRepository;
import com.stockmanagement.stockmanagementsystem.repository.ProductRepository;
import com.stockmanagement.stockmanagementsystem.repository.StockTransactionRepository;
import com.stockmanagement.stockmanagementsystem.repository.SupplierRepository;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final ProductService productService;

    public ReportService(ProductRepository productRepository, CategoryRepository categoryRepository,
            SupplierRepository supplierRepository, StockTransactionRepository stockTransactionRepository,
            ProductService productService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.stockTransactionRepository = stockTransactionRepository;
        this.productService = productService;
    }

    public long getTotalProducts() {
        return productRepository.count();
    }

    public long getTotalCategories() {
        return categoryRepository.count();
    }

    public long getTotalSuppliers() {
        return supplierRepository.count();
    }

    public long getTotalStockQuantity() {
        return productRepository.sumCurrentStockQuantity();
    }

    public long getLowStockCount() {
        return productService.getLowStockCount();
    }

    public long getOutOfStockCount() {
        return productRepository.countByStatusAndQuantity(ProductStatus.ACTIVE, 0);
    }

    public List<StockTransaction> getRecentTransactions() {
        return stockTransactionRepository.findTop5ByOrderByTransactionDateDesc();
    }

    public List<Product> getCurrentStockReport() {
        return productService.getAllProducts();
    }

    public List<Product> getLowStockReport() {
        return productService.getLowStockProducts();
    }

    public List<StockTransaction> getTransactionReport(
            StockTransactionType transactionType, LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From date must not be after To date.");
        }
        LocalDateTime from = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toExclusive = toDate == null ? null : toDate.plusDays(1).atStartOfDay();
        return stockTransactionRepository.findForReport(transactionType, from, toExclusive);
    }
}
