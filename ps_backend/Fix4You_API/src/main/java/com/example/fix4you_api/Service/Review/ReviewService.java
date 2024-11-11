package com.example.fix4you_api.Service.Review;

import com.example.fix4you_api.Data.Models.Review;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReviewService {
    List<Review> getAllReviews();
    List<Review> getAllReviewsOfReviwedId(String id);
    ResponseEntity<?> createReview(Review review);
    void deleteReviewsForUser(String userId);
    Review getReview(String id);
}
