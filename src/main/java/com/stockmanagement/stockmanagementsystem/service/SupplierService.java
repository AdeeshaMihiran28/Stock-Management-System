package com.stockmanagement.stockmanagementsystem.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.stockmanagement.stockmanagementsystem.exception.ResourceNotFoundException;
import com.stockmanagement.stockmanagementsystem.model.Supplier;
import com.stockmanagement.stockmanagementsystem.repository.SupplierRepository;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
    }

    @Transactional
    public Supplier saveSupplier(Supplier supplier) {
        normalize(supplier);
        if (supplierRepository.existsBySupplierCodeIgnoreCase(supplier.getSupplierCode())) {
            throw new IllegalArgumentException("A supplier with this code already exists.");
        }
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplier) {
        Supplier existingSupplier = getSupplierById(id);
        normalize(supplier);
        if (supplierRepository.existsBySupplierCodeIgnoreCaseAndIdNot(supplier.getSupplierCode(), id)) {
            throw new IllegalArgumentException("A supplier with this code already exists.");
        }
        existingSupplier.setSupplierCode(supplier.getSupplierCode());
        existingSupplier.setName(supplier.getName());
        existingSupplier.setContactPerson(supplier.getContactPerson());
        existingSupplier.setPhone(supplier.getPhone());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setAddress(supplier.getAddress());
        existingSupplier.setStatus(supplier.getStatus());
        return supplierRepository.save(existingSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        try {
            supplierRepository.delete(supplier);
            supplierRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalStateException("Cannot delete this supplier because it is in use.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<Supplier> searchSuppliers(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllSuppliers();
        }
        return supplierRepository.search(keyword.trim());
    }

    private void normalize(Supplier supplier) {
        supplier.setSupplierCode(supplier.getSupplierCode().trim());
        supplier.setName(supplier.getName().trim());
        supplier.setPhone(supplier.getPhone().trim());
        supplier.setContactPerson(cleanOptional(supplier.getContactPerson()));
        supplier.setEmail(cleanOptional(supplier.getEmail()));
        supplier.setAddress(cleanOptional(supplier.getAddress()));
    }

    private String cleanOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
