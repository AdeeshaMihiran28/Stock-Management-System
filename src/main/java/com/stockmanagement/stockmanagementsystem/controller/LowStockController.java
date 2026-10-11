package com.stockmanagement.stockmanagementsystem.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.service.ProductService;

@Controller
public class LowStockController {

    private final ProductService productService;

    public LowStockController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/low-stock")
    public String listLowStockProducts(Model model) {
        List<Product> products = productService.getLowStockProducts();
        model.addAttribute("products", products);
        model.addAttribute("lowStockCount", products.size());
        return "low-stock/list";
    }
}
