package com.example.fix4you_api.Service.CategoryDescription;

import com.example.fix4you_api.Data.Models.CategoryDescription;

public interface CategoryDescriptionService {

    CategoryDescription createCategoryDescription(CategoryDescription categoryDescription);

    void deleteCategoryDescriptions(String professionalId);

}
