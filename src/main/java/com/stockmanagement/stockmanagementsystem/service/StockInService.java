package com.stockmanagement.stockmanagementsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.stockmanagement.stockmanagementsystem.exception.ResourceNotFoundException;
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

@Service
public class StockInService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockTransactionRepository stockTransactionRepository;

    public StockInService(ProductRepository productRepository, SupplierRepository supplierRepository,
            StockTransactionRepository stockTransactionRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.stockTransactionRepository = stockTransactionRepository;
    }

    @Transactional(readOnly = true)
    public List<StockTransaction> getStockInTransactions() {
        return stockTransactionRepository
                .findByTransactionTypeOrderByTransactionDateDesc(StockTransactionType.IN);
    }

    @Transactional
    public StockTransaction processStockIn(StockInForm form) {
        if (form.getQuantity() == null || form.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        Product product = productRepository.findById(form.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected product was not found."));
        Supplier supplier = supplierRepository.findById(form.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected supplier was not found."));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new IllegalArgumentException("Stock cannot be added to an inactive product.");
        }
        if (supplier.getStatus() != SupplierStatus.ACTIVE) {
            throw new IllegalArgumentException("An inactive supplier cannot be used for Stock-In.");
        }

        try {
            product.setQuantity(Math.addExact(product.getQuantity(), form.getQuantity()));
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("The resulting product quantity is too large.", exception);
        }
        productRepository.save(product);

        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setSupplier(supplier);
        transaction.setTransactionType(StockTransactionType.IN);
        transaction.setQuantity(form.getQuantity());
        transaction.setTransactionDate(form.getTransactionDate());
        transaction.setReferenceNumber(cleanOptional(form.getReferenceNumber()));
        transaction.setNotes(cleanOptional(form.getNotes()));
        return stockTransactionRepository.save(transaction);
    }

    private String cleanOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
