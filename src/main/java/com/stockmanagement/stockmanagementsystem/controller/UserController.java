package com.stockmanagement.stockmanagementsystem.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stockmanagement.stockmanagementsystem.model.PasswordForm;
import com.stockmanagement.stockmanagementsystem.model.UserForm;
import com.stockmanagement.stockmanagementsystem.model.UserRole;
import com.stockmanagement.stockmanagementsystem.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        prepareForm(model, new UserForm(), "Create User");
        return "users/form";
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute("userForm") UserForm form,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            result.rejectValue("password", "required", "Password is required.");
        }
        if (result.hasErrors()) {
            prepareForm(model, form, "Create User");
            return "users/form";
        }
        try {
            userService.createUser(form);
        } catch (IllegalArgumentException exception) {
            result.reject("user", exception.getMessage());
            prepareForm(model, form, "Create User");
            return "users/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        prepareForm(model, userService.toForm(userService.getUserById(id)), "Edit User");
        return "users/form";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
            @Valid @ModelAttribute("userForm") UserForm form, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            form.setId(id);
            prepareForm(model, form, "Edit User");
            return "users/form";
        }
        try {
            userService.updateUser(id, form);
        } catch (IllegalArgumentException exception) {
            result.reject("user", exception.getMessage());
            form.setId(id);
            prepareForm(model, form, "Edit User");
            return "users/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        return "redirect:/users";
    }

    @PostMapping("/users/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            userService.toggleStatus(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/password/{id}")
    public String passwordForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("passwordForm", new PasswordForm());
        return "users/password";
    }

    @PostMapping("/users/password/{id}")
    public String resetPassword(@PathVariable Long id,
            @Valid @ModelAttribute("passwordForm") PasswordForm form, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", userService.getUserById(id));
            return "users/password";
        }
        userService.resetPassword(id, form.getPassword());
        redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully.");
        return "redirect:/users";
    }

    private void prepareForm(Model model, UserForm form, String title) {
        model.addAttribute("userForm", form);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("pageTitle", title);
    }
}
