package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Professional;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryDescriptionRepository extends MongoRepository<CategoryDescription, String> {
    List<CategoryDescription> findByIdProfessional(String id);
}
