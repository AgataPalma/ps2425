package com.example.fix4you_api.Service.Category;

import com.example.fix4you_api.Data.Models.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryByName(String categoryName);
    Category createCategory(Category category);
    Category updateCategory(String id, Category category);
    Category partialUpdateCategory(String id, Map<String, Object> updates);
    void updateCategoryMinMaxValue(String id);
    void deleteCategory(String id);
    boolean nameExists(String name);
}
