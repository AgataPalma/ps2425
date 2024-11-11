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
    public ResponseEntity<?> addReview(@RequestBody Review review) {
        return new ResponseEntity<>(reviewService.createReview(review), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews() {
        List<Review> professionalsFee = reviewService.getAllReviews();
        return new ResponseEntity<>(professionalsFee, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Review>> GetUserReviews(@PathVariable String id) {
        List<Review> list = reviewService.getAllReviewsOfReviwedId(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(@PathVariable String id) {
        Review review = reviewService.getReview(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }
}