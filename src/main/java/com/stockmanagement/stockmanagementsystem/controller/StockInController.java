package com.stockmanagement.stockmanagementsystem.controller;

import java.time.LocalDateTime;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stockmanagement.stockmanagementsystem.exception.ResourceNotFoundException;
import com.stockmanagement.stockmanagementsystem.model.StockInForm;
import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.service.ProductService;
import com.stockmanagement.stockmanagementsystem.service.StockInService;
import com.stockmanagement.stockmanagementsystem.service.SupplierService;

import jakarta.validation.Valid;

@Controller
public class StockInController {

    private final StockInService stockInService;
    private final ProductService productService;
    private final SupplierService supplierService;

    public StockInController(StockInService stockInService, ProductService productService,
            SupplierService supplierService) {
        this.stockInService = stockInService;
        this.productService = productService;
        this.supplierService = supplierService;
    }

    @GetMapping("/stock-in")
    public String listStockIn(Model model) {
        model.addAttribute("transactions", stockInService.getStockInTransactions());
        return "stock-in/list";
    }

    @GetMapping("/stock-in/new")
    public String showStockInForm(@RequestParam(required = false) Long productId, Model model) {
        StockInForm form = new StockInForm();
        form.setProductId(productId);
        form.setTransactionDate(LocalDateTime.now().withSecond(0).withNano(0));
        prepareForm(model, form);
        return "stock-in/form";
    }

    @PostMapping("/stock-in")
    public String processStockIn(@Valid @ModelAttribute("stockInForm") StockInForm form,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form);
            return "stock-in/form";
        }

        try {
            StockTransaction transaction = stockInService.processStockIn(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Stock-In completed successfully. " + transaction.getQuantity()
                            + " units were added to " + transaction.getProduct().getProductCode() + ".");
            return "redirect:/stock-in";
        } catch (ResourceNotFoundException | IllegalArgumentException exception) {
            bindingResult.reject("stockIn", exception.getMessage());
        } catch (DataAccessException exception) {
            bindingResult.reject("stockIn", "Stock-In could not be completed. Please try again.");
        }
        prepareForm(model, form);
        return "stock-in/form";
    }

    private void prepareForm(Model model, StockInForm form) {
        model.addAttribute("stockInForm", form);
        model.addAttribute("products", productService.getActiveProducts());
        model.addAttribute("suppliers", supplierService.getActiveSuppliers());
    }
}
