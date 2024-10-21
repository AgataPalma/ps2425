package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Professional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessionalRepository extends MongoRepository<Professional, String> {
}
