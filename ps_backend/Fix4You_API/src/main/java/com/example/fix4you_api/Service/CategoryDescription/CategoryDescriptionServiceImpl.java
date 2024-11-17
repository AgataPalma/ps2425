package com.example.fix4you_api.Service.CategoryDescription;

import com.example.fix4you_api.Data.Models.Category;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.MongoRepositories.CategoryDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryDescriptionServiceImpl implements CategoryDescriptionService {

    private final CategoryDescriptionRepository categoryDescriptionRepository;

    @Override
    public List<CategoryDescription> getAllCategoriesDescription() {
        return categoryDescriptionRepository.findAll();
    }

    @Override
    public CategoryDescription getCategoriesDescriptionById(String id) {
        return findOrThrow(id);
    }

    @Override
    public List<CategoryDescription> getCategoriesDescriptionByProfessionalId(String professionalId) {
        return categoryDescriptionRepository.findByProfessionalId(professionalId);
    }

    @Override
    public CategoryDescription createCategoryDescription(CategoryDescription categoryDescription) {
        return categoryDescriptionRepository.save(categoryDescription);
    }

    @Override
    @Transactional
    public CategoryDescription updatecategoryDescription(String id, CategoryDescription categoryDescription) {
        CategoryDescription existingCategoryDescription = findOrThrow(id);
        BeanUtils.copyProperties(categoryDescription, existingCategoryDescription, "id");
        return categoryDescriptionRepository.save(existingCategoryDescription);
    }

    @Override
    @Transactional
    public CategoryDescription partialUpdateCategoryDescription(String id, Map<String, Object> updates) {
        CategoryDescription existingCategory = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "categoryId" -> existingCategory.setCategoryId((String) value);
                case "chargesTravels" -> existingCategory.setChargesTravels((Boolean) value);
                case "providesInvoices" -> existingCategory.setProvidesInvoices((Boolean) value);
                case "mediumPricePerService" -> existingCategory.setMediumPricePerService(((Double) value).floatValue());
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

        return categoryDescriptionRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategoryDescriptionsById(String id) {
        categoryDescriptionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteCategoryDescriptionsByProfessionalId(String professionalId) {
        categoryDescriptionRepository.deleteByProfessionalId(professionalId);
    }

    private CategoryDescription findOrThrow(String id) {
        return categoryDescriptionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("CategoryDescription %s not found", id)));
    }

}