package com.example.fix4you_api.Service.CategoryDescription;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.MongoRepositories.CategoryDescriptionRepository;
import com.example.fix4you_api.Event.CategoryDescription.CategoryDescriptionCreationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryDescriptionServiceImplUnitTest {

    @Mock
    private CategoryDescriptionRepository categoryDescriptionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CategoryDescriptionServiceImpl categoryDescriptionService;

    private CategoryDescription testCategoryDescription;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        testCategoryDescription = new CategoryDescription();
        testCategoryDescription.setId("1");
        testCategoryDescription.setProfessionalId("prof123");
        testCategoryDescription.setMediumPricePerService(100f);
        testCategoryDescription.setChargesTravels(false);
    }

    @Test
    void testGetAllCategoriesDescription() {
        // Mock behavior
        when(categoryDescriptionRepository.findAll()).thenReturn(List.of(testCategoryDescription));

        // Test
        List<CategoryDescription> descriptions = categoryDescriptionService.getAllCategoriesDescription();

        // Verify and assert
        verify(categoryDescriptionRepository, times(1)).findAll();
        assertThat(descriptions).hasSize(1);
        assertThat(descriptions.get(0).getId()).isEqualTo("1");
    }

    @Test
    void testGetCategoryDescriptionById() {
        // Mock behavior
        when(categoryDescriptionRepository.findById("1")).thenReturn(Optional.of(testCategoryDescription));

        // Test
        CategoryDescription description = categoryDescriptionService.getCategoryDescriptionById("1");

        // Verify and assert
        verify(categoryDescriptionRepository, times(1)).findById("1");
        assertThat(description).isNotNull();
        assertThat(description.getId()).isEqualTo("1");
    }

    @Test
    void testGetCategoryDescriptionByIdNotFound() {
        // Mock behavior
        when(categoryDescriptionRepository.findById("1")).thenReturn(Optional.empty());

        // Test
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            categoryDescriptionService.getCategoryDescriptionById("1");
        });

        // Verify and assert
        verify(categoryDescriptionRepository, times(1)).findById("1");
        assertThat(exception.getMessage()).isEqualTo("Descrição da categoria 1 não encontrada!");
    }

    @Test
    void testCreateCategoryDescription() {
        // Mock behavior
        when(categoryDescriptionRepository.save(any(CategoryDescription.class))).thenReturn(testCategoryDescription);

        // Test
        CategoryDescription createdDescription = categoryDescriptionService.createCategoryDescription(testCategoryDescription);

        // Capture and verify event
        ArgumentCaptor<CategoryDescriptionCreationEvent> eventCaptor = ArgumentCaptor.forClass(CategoryDescriptionCreationEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        verify(categoryDescriptionRepository, times(1)).save(testCategoryDescription);

        // Assert
        assertThat(createdDescription).isNotNull();
        assertThat(eventCaptor.getValue().getCategoryDescription()).isEqualTo(testCategoryDescription);
    }

    @Test
    void testUpdateCategoryDescription() {
        // Mock behavior
        when(categoryDescriptionRepository.findById("1")).thenReturn(Optional.of(testCategoryDescription));
        when(categoryDescriptionRepository.save(any(CategoryDescription.class))).thenReturn(testCategoryDescription);

        // Test
        testCategoryDescription.setMediumPricePerService(120f);
        CategoryDescription updatedDescription = categoryDescriptionService.updatecategoryDescription("1", testCategoryDescription);

        // Verify and assert
        verify(categoryDescriptionRepository, times(1)).findById("1");
        verify(categoryDescriptionRepository, times(1)).save(testCategoryDescription);
        assertThat(updatedDescription.getMediumPricePerService()).isEqualTo(120f);
    }

    @Test
    void testPartialUpdateCategoryDescription() {
        // Mock behavior
        when(categoryDescriptionRepository.findById("1")).thenReturn(Optional.of(testCategoryDescription));
        when(categoryDescriptionRepository.save(any(CategoryDescription.class))).thenReturn(testCategoryDescription);

        // Test
        Map<String, Object> updates = Map.of("mediumPricePerService", 150.0);
        CategoryDescription updatedDescription = categoryDescriptionService.partialUpdateCategoryDescription("1", updates);

        // Verify and assert
        verify(categoryDescriptionRepository, times(1)).findById("1");
        verify(categoryDescriptionRepository, times(1)).save(testCategoryDescription);
        assertThat(updatedDescription.getMediumPricePerService()).isEqualTo(150f);
    }

    @Test
    void testDeleteCategoryDescriptionsById() {
        // Test
        categoryDescriptionService.deleteCategoryDescriptionById("1");

        // Verify
        verify(categoryDescriptionRepository, times(1)).deleteById("1");
    }

    @Test
    void testDeleteCategoryDescriptionsForProfessional() {
        // Test
        categoryDescriptionService.deleteCategoryDescriptionsForProfessional("prof123");

        // Verify
        verify(categoryDescriptionRepository, times(1)).deleteByProfessionalId("prof123");
    }
}
