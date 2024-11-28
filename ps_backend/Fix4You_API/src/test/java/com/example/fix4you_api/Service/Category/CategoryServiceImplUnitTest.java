package com.example.fix4you_api.Service.Category;

import com.example.fix4you_api.Data.Models.Category;
import com.example.fix4you_api.Data.MongoRepositories.CategoryRepository;
import com.example.fix4you_api.Service.CategoryDescription.CategoryDescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CategoryServiceImplUnitTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryDescriptionService categoryDescriptionService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        testCategory = new Category();
        testCategory.setId("1");
        testCategory.setName("Test Category");
        testCategory.setMinValue(0f);
        testCategory.setMaxValue(0f);
    }

    @Test
    void testCreateCategory() {
        // Mock behavior
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Test createCategory
        Category createdCategory = categoryService.createCategory(testCategory);

        // Verify and assert
        verify(categoryRepository, times(1)).save(testCategory);
        assertThat(createdCategory).isNotNull();
        assertThat(createdCategory.getName()).isEqualTo("Test Category");
    }

    @Test
    void testGetCategoryByName() {
        // Mock behavior
        when(categoryRepository.findByName("Test Category")).thenReturn(testCategory);

        // Test getCategoryByName
        Category foundCategory = categoryService.getCategoryByName("Test Category");

        // Verify and assert
        verify(categoryRepository, times(1)).findByName("Test Category");
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getName()).isEqualTo("Test Category");
    }

    @Test
    void testUpdateCategory() {
        // Mock behavior
        when(categoryRepository.findById("1")).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Test updateCategory
        testCategory.setName("Updated Category");
        Category updatedCategory = categoryService.updateCategory("1", testCategory);

        // Verify and assert
        verify(categoryRepository, times(1)).findById("1");
        verify(categoryRepository, times(1)).save(testCategory);
        assertThat(updatedCategory.getName()).isEqualTo("Updated Category");
    }

    @Test
    void testDeleteCategory() {
        // Test deleteCategory
        categoryService.deleteCategory("1");

        // Verify
        verify(categoryRepository, times(1)).deleteById("1");
    }

    @Test
    void testGetCategoryByIdNotFound() {
        // Mock behavior
        when(categoryRepository.findById("1")).thenReturn(Optional.empty());

        // Test findOrThrow
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            categoryService.updateCategory("1", testCategory);
        });

        // Verify and assert
        verify(categoryRepository, times(1)).findById("1");
        assertThat(exception.getMessage()).isEqualTo("Categoria 1 não encontrada!");
    }
}
