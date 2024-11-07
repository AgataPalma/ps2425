package com.example.fix4you_api.Service.Review;

import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.Review;
import com.example.fix4you_api.Data.MongoRepositories.ReviewRepository;
import com.example.fix4you_api.Service.Client.ClientService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProfessionalService professionalService;
    private final ClientService clientService;

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReviewsForClient(String clientId) {
        reviewRepository.deleteByClientId(clientId);
    }

    @Override
    @Transactional
    public void deleteReviewsForProfessional(String professionalId) {
        reviewRepository.deleteByProfessionalId(professionalId);
    }

}