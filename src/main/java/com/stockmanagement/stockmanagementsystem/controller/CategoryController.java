package com.stockmanagement.stockmanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stockmanagement.stockmanagementsystem.model.Category;
import com.stockmanagement.stockmanagementsystem.service.CategoryService;

import jakarta.validation.Valid;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/list";
    }

    @GetMapping("/categories/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Add Category");
        return "categories/form";
    }

    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add Category");
            return "categories/form";
        }
        try {
            categoryService.saveCategory(category);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("name", "duplicate", exception.getMessage());
            model.addAttribute("pageTitle", "Add Category");
            return "categories/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Category created successfully.");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        model.addAttribute("pageTitle", "Edit Category");
        return "categories/form";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable Long id,
            @Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Category");
            return "categories/form";
        }
        try {
            categoryService.updateCategory(id, category);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("name", "duplicate", exception.getMessage());
            model.addAttribute("pageTitle", "Edit Category");
            return "categories/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully.");
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/categories";
    }
}
