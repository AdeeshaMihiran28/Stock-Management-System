package com.stockmanagement.stockmanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.ProductStatus;
import com.stockmanagement.stockmanagementsystem.service.CategoryService;
import com.stockmanagement.stockmanagementsystem.service.ProductService;

import jakarta.validation.Valid;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listProducts(@RequestParam(required = false, defaultValue = "") String keyword, Model model) {
        model.addAttribute("products", productService.searchProducts(keyword));
        model.addAttribute("keyword", keyword);
        return "products/list";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        prepareForm(model, new Product(), "Add Product");
        return "products/form";
    }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, product, "Add Product");
            return "products/form";
        }
        try {
            productService.saveProduct(product);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("productCode", "duplicate", exception.getMessage());
            prepareForm(model, product, "Add Product");
            return "products/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Product created successfully.");
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        prepareForm(model, productService.getProductById(id), "Edit Product");
        return "products/form";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, product, "Edit Product");
            return "products/form";
        }
        try {
            productService.updateProduct(id, product);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("productCode", "duplicate", exception.getMessage());
            prepareForm(model, product, "Edit Product");
            return "products/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully.");
        return "redirect:/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/products";
    }

    private void prepareForm(Model model, Product product, String pageTitle) {
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", ProductStatus.values());
        model.addAttribute("pageTitle", pageTitle);
    }
}
