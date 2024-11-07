package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {
    void deleteByProfessionalId(String professionalId);
    void deleteByClientId(String clientId);
}