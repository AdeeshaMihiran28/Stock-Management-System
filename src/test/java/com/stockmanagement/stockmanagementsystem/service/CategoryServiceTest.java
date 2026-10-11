package com.stockmanagement.stockmanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmanagement.stockmanagementsystem.model.Category;
import com.stockmanagement.stockmanagementsystem.repository.CategoryRepository;
import com.stockmanagement.stockmanagementsystem.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void rejectsDuplicateCategoryName() {
        Category category = new Category();
        category.setName(" Electronics ");
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.saveCategory(category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A category with this name already exists.");
        verify(categoryRepository, never()).save(category);
    }

    @Test
    void preventsDeletingCategoryUsedByProducts() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot delete this category because products are assigned to it.");
        verify(categoryRepository, never()).delete(category);
    }
}
