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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {
/*
    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ScheduleAppointmentRepository scheduleAppointmentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @Mock
    private ClientService clientService;

    @Mock
    private ProfessionalService professionalService;

    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;

    public ReviewServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    private String serviceId = "";
    private String reviewerId = "";
    private String reviewedId = "";


    @Test
    void shouldReturnConflictWhenClassificationIsInvalid() {
        Review review = new Review();
        review.setClassification(6); // Invalid classification

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Invalid classification [1-5]", response.getBody());
    }

    @Test
    void shouldReturnConflictWhenServiceDoesNotExist() {
        Review review = new Review();
        review.setClassification(4);
        review.setServiceId(serviceId);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Service doesnt exist", response.getBody());
    }

    @Test
    void shouldReturnConflictWhenServiceIsNotCompleted() {
        Review review = new Review();
        review.setClassification(4);
        review.setServiceId(serviceId);

        Service service = new Service();
        service.setState(ServiceStateEnum.PENDING);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Service is not completed yet", response.getBody());
    }

    @Test
    void shouldReturnConflictWhenNoCompletedScheduleAppointmentFound() {
        Review review = new Review();
        review.setClassification(4);
        review.setServiceId(serviceId);

        Service service = new Service();
        service.setState(ServiceStateEnum.COMPLETED);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(scheduleAppointmentRepository.findByServiceId(serviceId)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Schedule appointment not found", response.getBody());
    }

    @Test
    void shouldReturnConflictWhenReviewDateIsInvalid() {
        Review review = new Review();
        review.setClassification(4);
        review.setServiceId(serviceId);

        Service service = new Service();
        service.setState(ServiceStateEnum.COMPLETED);

        ScheduleAppointment scheduleAppointment = new ScheduleAppointment();
        scheduleAppointment.setState(ScheduleStateEnum.COMPLETED);
        scheduleAppointment.setDateFinish(LocalDateTime.now().minusMonths(4)); // Outside 3-month range

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(scheduleAppointmentRepository.findByServiceId(serviceId)).thenReturn(Collections.singletonList(scheduleAppointment));

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Date to make the review as expired", response.getBody());
    }

    @Test
    void shouldReturnBadRequestWhenReviewerDoesNotExist() {
        Review review = new Review();
        review.setClassification(4);
        review.setReviewerId(reviewerId);

        when(userService.getUserById(reviewerId)).thenReturn(null);

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Reviewer doesnt exist", response.getBody());
    }

    @Test
    void shouldReturnBadRequestWhenReviewedDoesNotExist() {
        Review review = new Review();
        review.setClassification(4);
        review.setReviewerId(reviewerId);
        review.setReviewedId(reviewedId);

        User reviewer = new User();

        when(userService.getUserById(reviewerId)).thenReturn(reviewer);
        when(userService.getUserById(reviewedId)).thenReturn(null);

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Reviewed doesnt exist", response.getBody());
    }

    @Test
    void shouldReturnBadRequestWhenReviewerAndReviewedHaveInvalidUserTypes() {
        Review review = new Review();
        review.setClassification(4);
        review.setReviewerId(reviewerId);
        review.setReviewedId(reviewedId);

        User reviewer = new User();
        reviewer.setUserType(EnumUserType.ADMIN);

        User reviewed = new User();
        reviewed.setUserType(EnumUserType.CLIENT);

        when(userService.getUserById(reviewerId)).thenReturn(reviewer);
        when(userService.getUserById(reviewedId)).thenReturn(reviewed);

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid user type", response.getBody());
    }

    @Test
    void shouldReturnConflictWhenReviewerAlreadyMadeReviewForService() {
        Review review = new Review();
        review.setClassification(4);
        review.setReviewerId(2L);
        review.setServiceId(1L);

        User reviewer = new User();
        reviewer.setUserType(EnumUserType.CLIENT);

        when(userService.getUserById(2L)).thenReturn(reviewer);
        when(reviewServiceImpl.CheckIfUserAlreadyMakeReviewToService(review, 2L)).thenReturn(true);

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("User already make review to this service", response.getBody());
    }

    @Test
    void shouldSaveReviewSuccessfully() {
        Review review = new Review();
        review.setClassification(4);
        review.setReviewerId(2L);
        review.setReviewedId(3L);
        review.setServiceId(1L);

        User reviewer = new User();
        reviewer.setUserType(EnumUserType.CLIENT);

        Client reviewed = new Client();
        reviewed.setUserType(EnumUserType.CLIENT);
        reviewed.setRating(3.5f);

        Service service = new Service();
        service.setState(ServiceStateEnum.COMPLETED);
        service.setProfessionalId(3L);
        service.setClientId(2L);

        ScheduleAppointment scheduleAppointment = new ScheduleAppointment();
        scheduleAppointment.setState(ScheduleStateEnum.COMPLETED);
        scheduleAppointment.setDateFinish(LocalDateTime.now().minusMonths(2));

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(scheduleAppointmentRepository.findByServiceId(1L)).thenReturn(Collections.singletonList(scheduleAppointment));
        when(userService.getUserById(2L)).thenReturn(reviewer);
        when(userService.getUserById(3L)).thenReturn(reviewed);
        when(reviewRepository.save(review)).thenReturn(review);

        ResponseEntity<?> response = reviewServiceImpl.createReview(review);

        assertEquals(200, response.getStatusCodeValue());
        verify(reviewRepository, times(1)).save(review);
    }*/
}