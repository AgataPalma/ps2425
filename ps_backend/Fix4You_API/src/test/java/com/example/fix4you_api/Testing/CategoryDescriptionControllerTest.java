package com.example.fix4you_api.Testing;

import com.example.fix4you_api.Controllers.CategoryDescriptionController;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Dtos.SimpleCategoryDTO;
import com.example.fix4you_api.Service.Category.CategoryService;
import com.example.fix4you_api.Service.CategoryDescription.CategoryDescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CategoryDescriptionControllerTest {

    @Mock
    private CategoryDescriptionService categoryDescriptionService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryDescriptionController categoryDescriptionController;

    private CategoryDescription mockCategoryDescription;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SimpleCategoryDTO mockCategory = new SimpleCategoryDTO();
        mockCategory.setId("cat123");
        mockCategory.setName("TestCategory");

        mockCategoryDescription = new CategoryDescription();
        mockCategoryDescription.setId("desc123");
        mockCategoryDescription.setProfessionalId("prof123");
        mockCategoryDescription.setCategory(mockCategory);
        mockCategoryDescription.setChargesTravels(true);
        mockCategoryDescription.setProvidesInvoices(false);
        mockCategoryDescription.setMediumPricePerService(100.50f);
    }

    @Test
    void testAddCategoryDescription() {
        when(categoryDescriptionService.createCategoryDescription(any(CategoryDescription.class)))
                .thenReturn(mockCategoryDescription);

        ResponseEntity<CategoryDescription> response = categoryDescriptionController.addCategoryDescription(mockCategoryDescription);

        verify(categoryDescriptionService).createCategoryDescription(mockCategoryDescription);
        verify(categoryService).updateCategoryMinMaxValue(mockCategoryDescription.getCategory().getId());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockCategoryDescription, response.getBody());
    }

    @Test
    void testGetAllCategoriesDescription() {
        when(categoryDescriptionService.getAllCategoriesDescription()).thenReturn(List.of(mockCategoryDescription));

        ResponseEntity<List<CategoryDescription>> response = categoryDescriptionController.getAllCategoriesDescription();

        verify(categoryDescriptionService).getAllCategoriesDescription();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(mockCategoryDescription, response.getBody().get(0));
    }

    @Test
    void testGetAllCategoriesDescription_NoData() {
        when(categoryDescriptionService.getAllCategoriesDescription()).thenReturn(Collections.emptyList());

        ResponseEntity<List<CategoryDescription>> response = categoryDescriptionController.getAllCategoriesDescription();

        verify(categoryDescriptionService).getAllCategoriesDescription();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetUserCategoryDescriptions() {
        String professionalId = "prof123";
        when(categoryDescriptionService.getCategoriesDescriptionByProfessionalId(professionalId))
                .thenReturn(List.of(mockCategoryDescription));

        ResponseEntity<List<CategoryDescription>> response = categoryDescriptionController.getUserCategoryDescriptions(professionalId);

        verify(categoryDescriptionService).getCategoriesDescriptionByProfessionalId(professionalId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(mockCategoryDescription, response.getBody().get(0));
    }

    @Test
    void testGetUserCategoryDescriptions_InvalidUserId() {
        String invalidProfessionalId = "invalidProf";
        when(categoryDescriptionService.getCategoriesDescriptionByProfessionalId(invalidProfessionalId))
                .thenThrow(new IllegalArgumentException("Professional ID not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                categoryDescriptionController.getUserCategoryDescriptions(invalidProfessionalId)
        );

        verify(categoryDescriptionService).getCategoriesDescriptionByProfessionalId(invalidProfessionalId);
        assertEquals("Professional ID not found", exception.getMessage());
    }

    @Test
    void testGetCategoryDescription() {
        String id = "desc123";
        when(categoryDescriptionService.getCategoryDescriptionById(id)).thenReturn(mockCategoryDescription);

        ResponseEntity<CategoryDescription> response = categoryDescriptionController.getCategoryDescription(id);

        verify(categoryDescriptionService).getCategoryDescriptionById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCategoryDescription, response.getBody());
    }

    @Test
    void testGetCategoryDescription_InvalidId() {
        String invalidId = "invalidDesc";
        when(categoryDescriptionService.getCategoryDescriptionById(invalidId))
                .thenThrow(new IllegalArgumentException("Category description not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                categoryDescriptionController.getCategoryDescription(invalidId)
        );

        verify(categoryDescriptionService).getCategoryDescriptionById(invalidId);
        assertEquals("Category description not found", exception.getMessage());
    }

    @Test
    void testUpdateCategoryDescription() {
        String id = "desc123";
        when(categoryDescriptionService.updatecategoryDescription(eq(id), any(CategoryDescription.class)))
                .thenReturn(mockCategoryDescription);

        ResponseEntity<CategoryDescription> response = categoryDescriptionController.updateCategoryDescription(id, mockCategoryDescription);

        verify(categoryDescriptionService).updatecategoryDescription(id, mockCategoryDescription);
        verify(categoryService).updateCategoryMinMaxValue(mockCategoryDescription.getCategory().getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCategoryDescription, response.getBody());
    }

    @Test
    void testUpdateCategoryDescription_InvalidId() {
        String invalidId = "invalidDesc";
        when(categoryDescriptionService.updatecategoryDescription(eq(invalidId), any(CategoryDescription.class)))
                .thenThrow(new IllegalArgumentException("Unable to update category description"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                categoryDescriptionController.updateCategoryDescription(invalidId, new CategoryDescription())
        );

        verify(categoryDescriptionService).updatecategoryDescription(eq(invalidId), any(CategoryDescription.class));
        assertEquals("Unable to update category description", exception.getMessage());
    }

    @Test
    void testPartialUpdateCategoryDescription() {
        String id = "desc123";
        Map<String, Object> updates = Map.of("mediumPricePerService", 120.75f);
        when(categoryDescriptionService.partialUpdateCategoryDescription(eq(id), eq(updates)))
                .thenReturn(mockCategoryDescription);

        ResponseEntity<CategoryDescription> response = categoryDescriptionController.partialUpdateCategoryDescription(id, updates);

        verify(categoryDescriptionService).partialUpdateCategoryDescription(id, updates);
        verify(categoryService).updateCategoryMinMaxValue(mockCategoryDescription.getCategory().getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCategoryDescription, response.getBody());
    }

    @Test
    void testPartialUpdateCategoryDescription_InvalidField() {
        String id = "desc123";
        Map<String, Object> invalidUpdates = Map.of("nonExistentField", "value");
        when(categoryDescriptionService.partialUpdateCategoryDescription(eq(id), eq(invalidUpdates)))
                .thenThrow(new IllegalArgumentException("Invalid field in updates"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                categoryDescriptionController.partialUpdateCategoryDescription(id, invalidUpdates)
        );

        verify(categoryDescriptionService).partialUpdateCategoryDescription(eq(id), eq(invalidUpdates));
        assertEquals("Invalid field in updates", exception.getMessage());
    }

    @Test
    void testDeleteCategoryDescription() {
        String id = "desc123";
        when(categoryDescriptionService.getCategoryDescriptionById(id)).thenReturn(mockCategoryDescription);

        ResponseEntity<Void> response = categoryDescriptionController.deleteCategoryDescription(id);

        verify(categoryDescriptionService).getCategoryDescriptionById(id);
        verify(categoryDescriptionService).deleteCategoryDescriptionsById(id);
        verify(categoryService).updateCategoryMinMaxValue(mockCategoryDescription.getCategory().getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteCategoryDescription_InvalidId() {
        String invalidId = "invalidDesc";
        when(categoryDescriptionService.getCategoryDescriptionById(invalidId))
                .thenThrow(new IllegalArgumentException("Category description not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                categoryDescriptionController.deleteCategoryDescription(invalidId)
        );

        verify(categoryDescriptionService).getCategoryDescriptionById(invalidId);
        verify(categoryDescriptionService, never()).deleteCategoryDescriptionsById(any());
        assertEquals("Category description not found", exception.getMessage());
    }
}

