package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryDescriptionRepository extends MongoRepository<CategoryDescription, String> {
    List<CategoryDescription> findByProfessionalId(String professionalId);
    List<CategoryDescription> findByProfessionalIdAndCategory_Id(String professionalId, String categoryId);
    List<CategoryDescription> findByCategory_Id(String categoryId);
    void deleteByProfessionalId(String professionalId);
}