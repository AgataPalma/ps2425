package com.example.fix4you_api.Service.CategoryDescription;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.MongoRepositories.CategoryDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryDescriptionServiceImpl implements CategoryDescriptionService {

    private final CategoryDescriptionRepository categoryDescriptionRepository;

    @Override
    public CategoryDescription createCategoryDescription(CategoryDescription categoryDescription) {
        return categoryDescriptionRepository.save(categoryDescription);
    }

    @Override
    @Transactional
    public void deleteCategoryDescriptions(String professionalId) {
        categoryDescriptionRepository.deleteByProfessionalId(professionalId);
    }

}
