package com.stockmanagement.stockmanagementsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "suppliers", uniqueConstraints = @UniqueConstraint(
        name = "uk_suppliers_supplier_code", columnNames = "supplier_code"))
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier code is required.")
    @Size(max = 50, message = "Supplier code must not exceed 50 characters.")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Supplier code may contain only letters, numbers, dots, hyphens, and underscores.")
    @Column(name = "supplier_code", nullable = false, length = 50)
    private String supplierCode;

    @NotBlank(message = "Supplier name is required.")
    @Size(max = 150, message = "Supplier name must not exceed 150 characters.")
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 150, message = "Contact person must not exceed 150 characters.")
    @Column(name = "contact_person", length = 150)
    private String contactPerson;

    @NotBlank(message = "Phone is required.")
    @Size(max = 20, message = "Phone must not exceed 20 characters.")
    @Pattern(regexp = "^\\+?[0-9][0-9 ()-]{5,18}[0-9]$", message = "Enter a valid phone number with 7 to 20 characters.")
    @Column(nullable = false, length = 20)
    private String phone;

    @Email(message = "Enter a valid email address.")
    @Size(max = 150, message = "Email must not exceed 150 characters.")
    @Column(length = 150)
    private String email;

    @Size(max = 500, message = "Address must not exceed 500 characters.")
    @Column(length = 500)
    private String address;

    @NotNull(message = "Status is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SupplierStatus status;

    public Supplier() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public SupplierStatus getStatus() {
        return status;
    }

    public void setStatus(SupplierStatus status) {
        this.status = status;
    }
}
