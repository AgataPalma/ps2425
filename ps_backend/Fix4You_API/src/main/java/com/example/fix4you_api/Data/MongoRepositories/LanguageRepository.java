package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Language;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LanguageRepository extends MongoRepository<Language, String> {
}