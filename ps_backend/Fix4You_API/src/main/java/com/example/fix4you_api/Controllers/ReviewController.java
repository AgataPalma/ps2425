package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.Review;
import com.example.fix4you_api.Service.Review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        review.setDate(LocalDateTime.now());
        Review createdReview = reviewService.createReview(review);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews() {
        List<Review> professionalsFee = reviewService.getAllReviews();
        return new ResponseEntity<>(professionalsFee, HttpStatus.OK);
    }

}