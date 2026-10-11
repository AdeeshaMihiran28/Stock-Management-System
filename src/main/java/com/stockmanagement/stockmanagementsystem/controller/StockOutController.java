package com.stockmanagement.stockmanagementsystem.controller;

import java.time.LocalDateTime;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stockmanagement.stockmanagementsystem.exception.ResourceNotFoundException;
import com.stockmanagement.stockmanagementsystem.model.StockOutForm;
import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.service.ProductService;
import com.stockmanagement.stockmanagementsystem.service.StockOutService;

import jakarta.validation.Valid;

@Controller
public class StockOutController {

    private final StockOutService stockOutService;
    private final ProductService productService;

    public StockOutController(StockOutService stockOutService, ProductService productService) {
        this.stockOutService = stockOutService;
        this.productService = productService;
    }

    @GetMapping("/stock-out")
    public String listStockOut(Model model) {
        model.addAttribute("transactions", stockOutService.getStockOutTransactions());
        return "stock-out/list";
    }

    @GetMapping("/stock-out/new")
    public String showStockOutForm(Model model) {
        StockOutForm form = new StockOutForm();
        form.setTransactionDate(LocalDateTime.now().withSecond(0).withNano(0));
        prepareForm(model, form);
        return "stock-out/form";
    }

    @PostMapping("/stock-out")
    public String processStockOut(@Valid @ModelAttribute("stockOutForm") StockOutForm form,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form);
            return "stock-out/form";
        }

        try {
            StockTransaction transaction = stockOutService.processStockOut(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Stock-Out completed successfully. " + transaction.getQuantity()
                            + " units were removed from " + transaction.getProduct().getProductCode() + ".");
            return "redirect:/stock-out";
        } catch (ResourceNotFoundException | IllegalArgumentException exception) {
            bindingResult.reject("stockOut", exception.getMessage());
        } catch (DataAccessException exception) {
            bindingResult.reject("stockOut", "Stock-Out could not be completed. Please try again.");
        }
        prepareForm(model, form);
        return "stock-out/form";
    }

    private void prepareForm(Model model, StockOutForm form) {
        model.addAttribute("stockOutForm", form);
        model.addAttribute("products", productService.getActiveProducts());
    }
}
