package com.example.fix4you_api.Service.CategoryDescription;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Service;

import java.util.List;
import java.util.Map;

public interface CategoryDescriptionService {
    List<CategoryDescription> getAllCategoriesDescription();
    CategoryDescription getCategoryDescriptionById(String id);
    List<CategoryDescription> getCategoriesDescriptionByProfessionalId(String professionalId);
    List<CategoryDescription> getCategoriesDescriptionByProfessionalIdAndCategoryId(String professionalId, String categoryId);
    List<CategoryDescription> getCategoriesDescriptionByCategoryId(String categoryId);
    CategoryDescription createCategoryDescription(CategoryDescription categoryDescription);
    CategoryDescription updatecategoryDescription(String id, CategoryDescription categoryDescription);
    CategoryDescription partialUpdateCategoryDescription(String id, Map<String, Object> updates);
    void deleteCategoryDescriptionsById(String id);
    void deleteCategoryDescriptionsForProfessional(String professionalId);

}
