package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Category findByName(String name);
}