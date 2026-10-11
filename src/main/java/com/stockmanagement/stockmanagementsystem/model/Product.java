package com.stockmanagement.stockmanagementsystem.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(name = "uk_products_product_code", columnNames = "product_code"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product code is required.")
    @Size(max = 50, message = "Product code must not exceed 50 characters.")
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @NotBlank(message = "Product name is required.")
    @Size(max = 150, message = "Product name must not exceed 150 characters.")
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    @Column(length = 500)
    private String description;

    @NotNull(message = "Category is required.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull(message = "Quantity is required.")
    @Min(value = 0, message = "Quantity cannot be negative.")
    @Column(nullable = false)
    private Integer quantity = 0;

    @NotNull(message = "Reorder level is required.")
    @Min(value = 0, message = "Reorder level cannot be negative.")
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 0;

    @NotNull(message = "Unit price is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative.")
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @NotNull(message = "Status is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
