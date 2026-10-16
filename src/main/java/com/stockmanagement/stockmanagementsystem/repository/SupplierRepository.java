package com.stockmanagement.stockmanagementsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockmanagement.stockmanagementsystem.model.Supplier;
import com.stockmanagement.stockmanagementsystem.model.SupplierStatus;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsBySupplierCodeIgnoreCase(String supplierCode);

    boolean existsBySupplierCodeIgnoreCaseAndIdNot(String supplierCode, Long id);

    List<Supplier> findByStatusOrderByNameAsc(SupplierStatus status);

    @Query("""
            SELECT supplier FROM Supplier supplier
            WHERE LOWER(supplier.supplierCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(supplier.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(supplier.contactPerson, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(supplier.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(supplier.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY supplier.name
            """)
    List<Supplier> search(@Param("keyword") String keyword);
}
