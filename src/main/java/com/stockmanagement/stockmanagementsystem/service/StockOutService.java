package com.stockmanagement.stockmanagementsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.stockmanagement.stockmanagementsystem.exception.ResourceNotFoundException;
import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.ProductStatus;
import com.stockmanagement.stockmanagementsystem.model.StockOutForm;
import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;
import com.stockmanagement.stockmanagementsystem.repository.ProductRepository;
import com.stockmanagement.stockmanagementsystem.repository.StockTransactionRepository;

@Service
public class StockOutService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;

    public StockOutService(ProductRepository productRepository,
            StockTransactionRepository stockTransactionRepository) {
        this.productRepository = productRepository;
        this.stockTransactionRepository = stockTransactionRepository;
    }

    @Transactional(readOnly = true)
    public List<StockTransaction> getStockOutTransactions() {
        return stockTransactionRepository
                .findByTransactionTypeOrderByTransactionDateDesc(StockTransactionType.OUT);
    }

    @Transactional
    public StockTransaction processStockOut(StockOutForm form) {
        if (form.getQuantity() == null || form.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        Product product = productRepository.findById(form.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected product was not found."));
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new IllegalArgumentException("This product is inactive.");
        }
        if (form.getQuantity() > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available quantity is " + product.getQuantity() + ".");
        }

        product.setQuantity(product.getQuantity() - form.getQuantity());
        productRepository.save(product);

        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setSupplier(null);
        transaction.setTransactionType(StockTransactionType.OUT);
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
