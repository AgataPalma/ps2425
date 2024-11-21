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
import com.example.fix4you_api.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Classificação inválida [1-5]!");
        }

        // check if service exists
        Optional<com.example.fix4you_api.Data.Models.Service> service = serviceRepository.findById(review.getServiceId());
        if(!service.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Serviço não encontrado!");
        }

        if(!service.get().getState().equals(ServiceStateEnum.COMPLETED)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("O serviço ainda não está completo!");
        }

        List<ScheduleAppointment> scheduleAppointmentList = scheduleAppointmentRepository.findByServiceId(review.getServiceId());
        LocalDateTime scheduleDate = null;
        for(ScheduleAppointment scheduleAppointment : scheduleAppointmentList) {
            if(scheduleAppointment.getState().equals(ScheduleStateEnum.COMPLETED)) {
                scheduleDate = scheduleAppointment.getDateFinish();
            }
        }

        if(scheduleDate == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Marcação do serviço não encontrada!");
        }

        // check if review date is valid (can only be made within 3 months)
        if(review.getDate().isAfter(scheduleDate.plusMonths(3))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A data para fazer a avaliação do serviço já expirou!");
        }

        // check if reviewer exists
        User reviewer = userService.getUserById(review.getReviewerId());
        if(reviewer == null ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O utilizador revisor não existe!");
        }

        // check if reviewed exists
        User reviewed = userService.getUserById(review.getReviewedId());
        if(reviewed == null ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O utilizador revisto não existe");
        }

        if(!service.get().getProfessionalId().equals(reviewed.getId()) && !service.get().getProfessionalId().equals(reviewer.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profissional não encontrado no serviço!");
        }

        if(!service.get().getClientId().equals(reviewed.getId()) && !service.get().getClientId().equals(reviewer.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente não encontrado no serviço!");
        }

        // check if users types are valid
        if(reviewer.getUserType() == EnumUserType.ADMIN || reviewed.getUserType() == EnumUserType.ADMIN) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de utilizador inválido!");
        }

        if(reviewer.getUserType() == reviewed.getUserType()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Os tipos de utilizador não podem ser os mesmos!");
        }

        if(CheckIfUserAlreadyMakeReviewToService(review, reviewer.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("O utilizador já fez uma avaliação a este serviço!");
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