package com.example.fix4you_api.Service.CategoryDescription;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.MongoRepositories.CategoryDescriptionRepository;
import com.example.fix4you_api.Event.CategoryDescription.CategoryDescriptionCreationEvent;
import com.example.fix4you_api.Service.Category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryDescriptionServiceImpl implements CategoryDescriptionService {

    private final CategoryDescriptionRepository categoryDescriptionRepository;

    private final CategoryService categoryService;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<CategoryDescription> getAllCategoriesDescription() {
        return categoryDescriptionRepository.findAll();
    }

    @Override
    public CategoryDescription getCategoryDescriptionById(String id) {
        return findOrThrow(id);
    }

    @Override
    public List<CategoryDescription> getCategoriesDescriptionByProfessionalId(String professionalId) {
        return categoryDescriptionRepository.findByProfessionalId(professionalId);
    }

    @Override
    public List<CategoryDescription> getCategoriesDescriptionByProfessionalIdAndCategoryId(String professionalId, String categoryId) {
        return categoryDescriptionRepository.findByProfessionalIdAndCategory_Id(professionalId, categoryId);
    }

    @Override
    public List<CategoryDescription> getCategoriesDescriptionByCategoryId(String categoryId) {
        return categoryDescriptionRepository.findByCategory_Id(categoryId);
    }

    @Override
    public CategoryDescription createCategoryDescription(CategoryDescription categoryDescription) {
        CategoryDescription newCategoryDescription = categoryDescriptionRepository.save(categoryDescription);
        categoryService.updateCategoryMinMaxValue(newCategoryDescription.getCategory().getId());
        eventPublisher.publishEvent(new CategoryDescriptionCreationEvent(this, newCategoryDescription));
        return newCategoryDescription;
    }

    @Override
    @Transactional
    public CategoryDescription updatecategoryDescription(String id, CategoryDescription categoryDescription) {
        CategoryDescription existingCategoryDescription = findOrThrow(id);
        BeanUtils.copyProperties(categoryDescription, existingCategoryDescription, "id");

        CategoryDescription categoryDescriptionUpdated = categoryDescriptionRepository.save(existingCategoryDescription);
        categoryService.updateCategoryMinMaxValue(existingCategoryDescription.getCategory().getId());
        return categoryDescriptionUpdated;
    }

    @Override
    @Transactional
    public CategoryDescription partialUpdateCategoryDescription(String id, Map<String, Object> updates) {
        CategoryDescription existingCategoryDescription = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "chargesTravels" -> existingCategoryDescription.setChargesTravels((Boolean) value);
                //case "providesInvoices" -> existingCategory.setProvidesInvoices((Boolean) value);
                case "mediumPricePerService" -> existingCategoryDescription.setMediumPricePerService(((Double) value).floatValue());
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!");
            }
        });

        CategoryDescription categoryDescriptionUpdated = categoryDescriptionRepository.save(existingCategoryDescription);
        categoryService.updateCategoryMinMaxValue(existingCategoryDescription.getCategory().getId());
        return categoryDescriptionUpdated;
    }

    @Override
    @Transactional
    public void deleteCategoryDescriptionById(String id) {
        CategoryDescription existingCategory = findOrThrow(id);

        categoryDescriptionRepository.deleteById(id);

        categoryService.updateCategoryMinMaxValue(existingCategory.getCategory().getId());
    }

    @Override
    @Transactional
    public void deleteCategoryDescriptionsForProfessional(String professionalId) {
        List<CategoryDescription> categoryDescriptions = getCategoriesDescriptionByProfessionalId(professionalId);

        for (CategoryDescription categoryDescription: categoryDescriptions) {
            deleteCategoryDescriptionById(categoryDescription.getId());
        }
    }

    private CategoryDescription findOrThrow(String id) {
        return categoryDescriptionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Descrição da categoria %s não encontrada!", id)));
    }

}