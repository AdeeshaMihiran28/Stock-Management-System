package com.stockmanagement.stockmanagementsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductCodeIgnoreCase(String productCode);

    boolean existsByProductCodeIgnoreCaseAndIdNot(String productCode, Long id);

    boolean existsByCategoryId(Long categoryId);

    List<Product> findByStatusOrderByNameAsc(ProductStatus status);

    @Query("""
            SELECT product FROM Product product
            WHERE product.status = :status
              AND product.quantity <= product.reorderLevel
            ORDER BY product.quantity, product.name
            """)
    List<Product> findLowStockProducts(@Param("status") ProductStatus status);

    @Query("""
            SELECT COUNT(product) FROM Product product
            WHERE product.status = :status
              AND product.quantity <= product.reorderLevel
            """)
    long countLowStockProducts(@Param("status") ProductStatus status);

    @Query("SELECT COALESCE(SUM(product.quantity), 0) FROM Product product")
    long sumCurrentStockQuantity();

    long countByStatusAndQuantity(ProductStatus status, Integer quantity);

    List<Product> findByNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrderByNameAsc(
            String name, String productCode);
}
