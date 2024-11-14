package com.example.fix4you_api.Service.CategoryDescription;

import com.example.fix4you_api.Data.Models.CategoryDescription;

import java.util.List;

public interface CategoryDescriptionService {
    List<CategoryDescription> getAllCategoriesDescription();
    CategoryDescription getCategoriesDescriptionById(String id);
    List<CategoryDescription> getCategoriesDescriptionByProfessionalId(String professionalId);
    CategoryDescription createCategoryDescription(CategoryDescription categoryDescription);
    CategoryDescription updatecategoryDescription(String id, CategoryDescription categoryDescription);
    void deleteCategoryDescriptionsById(String id);
    void deleteCategoryDescriptionsByProfessionalId(String professionalId);

}
