package com.stockmanagement.stockmanagementsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmanagement.stockmanagementsystem.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductCodeIgnoreCase(String productCode);

    boolean existsByProductCodeIgnoreCaseAndIdNot(String productCode, Long id);

    boolean existsByCategoryId(Long categoryId);

    List<Product> findByNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrderByNameAsc(
            String name, String productCode);
}
