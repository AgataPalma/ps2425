package com.example.fix4you_api.Service.Review;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.*;
import com.example.fix4you_api.Data.MongoRepositories.ReviewRepository;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import com.example.fix4you_api.Data.MongoRepositories.ServiceRepository;
import com.example.fix4you_api.Service.Client.ClientService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.Service.ServiceService;
import com.example.fix4you_api.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForReadableInstant;
import org.springframework.boot.autoconfigure.couchbase.ClusterEnvironmentBuilderCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProfessionalService professionalService;
    private final ClientService clientService;
    private final ServiceRepository serviceRepository;
    private final ScheduleAppointmentRepository scheduleAppointmentRepository;

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public ResponseEntity<?> createReview(Review review) {
        review.setDate(LocalDateTime.now());
        Client reviwedUser;     // could be either a professional or a client

        if(review.getClassification() < 1 && review.getClassification() > 5) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Invalid classification [1-5]");
        }

        // check if service exists
        Optional<com.example.fix4you_api.Data.Models.Service> service = serviceRepository.findById(review.getServiceId());
        if(!service.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Service doesnt exist");
        }

        if(!service.get().getState().equals(ServiceStateEnum.COMPLETED)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Service is not completed yet");
        }

        List<ScheduleAppointment> scheduleAppointmentList = scheduleAppointmentRepository.findByServiceId(review.getServiceId());
        LocalDateTime scheduleDate = null;
        for(ScheduleAppointment scheduleAppointment : scheduleAppointmentList) {
            if(scheduleAppointment.getState().equals(ScheduleStateEnum.COMPLETED)) {
                scheduleDate = scheduleAppointment.getDateFinish();
            }
        }

        if(scheduleDate == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Schedule appointment not found");
        }

        // check if review date is valid (can only be made within 3 months)
        if(review.getDate().isAfter(scheduleDate.plusMonths(3))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Date to make the review as expired");
        }

        // check if reviewer exists
        User reviewer = userService.getUserById(review.getReviewerId());
        if(reviewer == null ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reviewer doesnt exist");
        }

        // check if reviewed exists
        User reviewed = userService.getUserById(review.getReviewedId());
        if(reviewed == null ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reviewed doesnt exist");
        }

        if(!service.get().getProfessionalId().equals(reviewed.getId()) && !service.get().getProfessionalId().equals(reviewer.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Professional not found in service");
        }

        if(!service.get().getClientId().equals(reviewed.getId()) && !service.get().getClientId().equals(reviewer.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Client not found in service");
        }

        // check if users types are valid
        if(reviewer.getUserType() == EnumUserType.ADMIN || reviewed.getUserType() == EnumUserType.ADMIN) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user type");
        }

        if(reviewer.getUserType() == reviewed.getUserType()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Users type can't be the same");
        }

        if(CheckIfUserAlreadyMakeReviewToService(review, reviewer.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already make review to this service");
        }

        reviwedUser = (Client) reviewed;
        int countReviewed = getAllReviewsOfReviwedId(reviewed.getId()).size();
        countReviewed++;

        float median = (reviwedUser.getRating() + review.getClassification()) / countReviewed;

        if(reviwedUser.getUserType() == EnumUserType.CLIENT) {
            clientService.setRating(median, reviwedUser);
        }
        else {
            professionalService.setRating(median, (Professional) reviwedUser);
        }

        return ResponseEntity.ok(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReviewsForUser(String userId) {
        reviewRepository.deleteReviewsByReviewedId(userId);
        reviewRepository.deleteReviewsByReviewerId(userId);
    }

    @Override
    public List<Review> getAllReviewsOfReviwedId(String id) {
        return reviewRepository.findReviewsByReviewedId(id);
    }

    @Override
    public Review getReview(String id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.isEmpty() ? null : review.get();
    }


    // a user can only make a review to a service one time
    private boolean CheckIfUserAlreadyMakeReviewToService(Review review, String userId) {
        List<Review> reviewsService = reviewRepository.findReviewsByServiceId(review.getServiceId());

        for(Review reviewService : reviewsService) {
            if(reviewService.getReviewerId().equals(userId)) {
                return true;
            }
        }

        return false;
    }

}