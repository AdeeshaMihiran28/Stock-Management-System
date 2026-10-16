package com.stockmanagement.stockmanagementsystem.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.stockmanagement.stockmanagementsystem.exception.ResourceNotFoundException;
import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.ProductStatus;
import com.stockmanagement.stockmanagementsystem.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findByStatusOrderByNameAsc(ProductStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts(ProductStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public long getLowStockCount() {
        return productRepository.countLowStockProducts(ProductStatus.ACTIVE);
    }

    @Transactional
    public Product saveProduct(Product product) {
        normalize(product);
        product.setQuantity(0);
        if (productRepository.existsByProductCodeIgnoreCase(product.getProductCode())) {
            throw new IllegalArgumentException("A product with this code already exists.");
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        normalize(product);
        if (productRepository.existsByProductCodeIgnoreCaseAndIdNot(product.getProductCode(), id)) {
            throw new IllegalArgumentException("A product with this code already exists.");
        }
        existingProduct.setProductCode(product.getProductCode());
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setReorderLevel(product.getReorderLevel());
        existingProduct.setUnitPrice(product.getUnitPrice());
        existingProduct.setStatus(product.getStatus());
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        try {
            productRepository.delete(product);
            productRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalStateException("Cannot delete this product because it is in use.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllProducts();
        }
        String searchTerm = keyword.trim();
        return productRepository.findByNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrderByNameAsc(
                searchTerm, searchTerm);
    }

    private void normalize(Product product) {
        product.setProductCode(product.getProductCode().trim());
        product.setName(product.getName().trim());
    }
}
