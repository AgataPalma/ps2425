package com.example.fix4you_api.Service.Review;

import com.example.fix4you_api.Data.Models.Review;

import java.util.List;

public interface ReviewService {
    List<Review> getAllReviews();
    Review createReview(Review review);
    void deleteReviewsForClient(String clientId);
    void deleteReviewsForProfessional(String professionalId);
}
