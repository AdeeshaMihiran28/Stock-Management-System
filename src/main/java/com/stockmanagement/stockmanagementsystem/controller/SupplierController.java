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

import com.stockmanagement.stockmanagementsystem.model.Supplier;
import com.stockmanagement.stockmanagementsystem.model.SupplierStatus;
import com.stockmanagement.stockmanagementsystem.service.SupplierService;

import jakarta.validation.Valid;

@Controller
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/suppliers")
    public String listSuppliers(@RequestParam(required = false, defaultValue = "") String keyword, Model model) {
        model.addAttribute("suppliers", supplierService.searchSuppliers(keyword));
        model.addAttribute("keyword", keyword);
        return "suppliers/list";
    }

    @GetMapping("/suppliers/new")
    public String showCreateForm(Model model) {
        prepareForm(model, new Supplier(), "Add Supplier");
        return "suppliers/form";
    }

    @PostMapping("/suppliers")
    public String createSupplier(@Valid @ModelAttribute("supplier") Supplier supplier,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, supplier, "Add Supplier");
            return "suppliers/form";
        }
        try {
            supplierService.saveSupplier(supplier);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("supplierCode", "duplicate", exception.getMessage());
            prepareForm(model, supplier, "Add Supplier");
            return "suppliers/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Supplier created successfully.");
        return "redirect:/suppliers";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        prepareForm(model, supplierService.getSupplierById(id), "Edit Supplier");
        return "suppliers/form";
    }

    @PostMapping("/suppliers/update/{id}")
    public String updateSupplier(@PathVariable Long id,
            @Valid @ModelAttribute("supplier") Supplier supplier,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, supplier, "Edit Supplier");
            return "suppliers/form";
        }
        try {
            supplierService.updateSupplier(id, supplier);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("supplierCode", "duplicate", exception.getMessage());
            prepareForm(model, supplier, "Edit Supplier");
            return "suppliers/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Supplier updated successfully.");
        return "redirect:/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            supplierService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier deleted successfully.");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/suppliers";
    }

    private void prepareForm(Model model, Supplier supplier, String pageTitle) {
        model.addAttribute("supplier", supplier);
        model.addAttribute("statuses", SupplierStatus.values());
        model.addAttribute("pageTitle", pageTitle);
    }
}
