package com.stockmanagement.stockmanagementsystem.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.stockmanagement.stockmanagementsystem.service.CategoryService;
import com.stockmanagement.stockmanagementsystem.service.ProductService;

@WebMvcTest({HomeController.class, CategoryController.class, ProductController.class})
class ControllerViewTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private ProductService productService;

    @Test
    void rendersHomeCategoryAndProductPages() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(productService.searchProducts("")).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Web-Based Stock Management System")));
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"));
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"));
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"));
    }

    @Test
    void returnsProductFormWithValidationMessages() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(post("/products")
                        .param("productCode", "P001")
                        .param("name", "Wireless Mouse")
                        .param("quantity", "-1")
                        .param("reorderLevel", "-1")
                        .param("unitPrice", "-1")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "product", "category", "quantity", "reorderLevel", "unitPrice"));
    }
}
