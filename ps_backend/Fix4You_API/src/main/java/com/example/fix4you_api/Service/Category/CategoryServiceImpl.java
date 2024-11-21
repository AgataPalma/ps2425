package com.example.fix4you_api.Service.Category;

import com.example.fix4you_api.Data.Models.Category;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.MongoRepositories.CategoryRepository;
import com.example.fix4you_api.Service.CategoryDescription.CategoryDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDescriptionService categoryDescriptionService;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }

    @Override
    public Category createCategory(Category category) {
        category.setMinValue(0f);
        category.setMaxValue(0f);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(String id, Category category) {
        Category existingCategory = findOrThrow(id);
        BeanUtils.copyProperties(category, existingCategory, "id");
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public Category partialUpdateCategory(String id, Map<String, Object> updates) {
        Category existingCategory = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existingCategory.setName((String) value);
                case "minValue" -> existingCategory.setMinValue((float) value);
                case "maxValue" -> existingCategory.setMaxValue((float) value);
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void updateCategoryMinMaxValue(String id) {
        List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getCategoriesDescriptionByCategoryId(id);
        Map<String, Object> updates = new HashMap<>();

        if (categoryDescriptions.isEmpty()) {
            updates.put("minValue", 0f);
            updates.put("maxValue", 0f);
        } else {

            double minPrice = categoryDescriptions.stream()
                    .mapToDouble(CategoryDescription::getMediumPricePerService)
                    .min()
                    .orElseThrow(() -> new RuntimeException("Não é possível calcular o preço mínimo!"));

            double maxPrice = categoryDescriptions.stream()
                    .mapToDouble(CategoryDescription::getMediumPricePerService)
                    .max()
                    .orElseThrow(() -> new RuntimeException("Não é possível calcular o preço máximo!"));


            updates.put("minValue", (float) minPrice);
            updates.put("maxValue", (float) maxPrice);
        }

        partialUpdateCategory(id, updates);
    }

    @Override
    @Transactional
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    private Category findOrThrow(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Categoria %s não encontrada!", id)));
    }

    @Override
    public boolean nameExists(String name) {
        List<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            if(category.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

}