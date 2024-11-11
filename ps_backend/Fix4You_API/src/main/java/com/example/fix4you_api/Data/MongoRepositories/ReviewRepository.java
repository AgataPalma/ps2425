package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findReviewsByReviewedId(String reviwedId);
    List<Review> findReviewsByServiceId(String serviceId);
    void deleteReviewsByReviewedId(String reviwedId);
    void deleteReviewsByReviewerId(String reviewerId);
}