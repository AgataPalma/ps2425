package com.example.fix4you_api.Service.Review;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.Review;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.Models.Service;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.ReviewRepository;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import com.example.fix4you_api.Data.MongoRepositories.ServiceRepository;
import com.example.fix4you_api.Service.Client.ClientService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.User.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProfessionalService professionalService;

    @Mock
    private ClientService clientService;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ScheduleAppointmentRepository scheduleAppointmentRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    public ReviewServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReview_positiveScenario() {
        String reviewerId = "reviewer-id";
        String reviewedId = "reviewed-id";
        String serviceId = "service-id";

        User reviewer = new Professional();
        reviewer.setId(reviewerId);
        reviewer.setUserType(EnumUserType.CLIENT);

        Professional reviewed = new Professional();
        reviewed.setId(reviewedId);
        reviewed.setUserType(EnumUserType.PROFESSIONAL);
        reviewed.setRating(4.0f);

        Service service = new Service();
        service.setId(serviceId);
        service.setProfessionalId(reviewedId);
        service.setClientId(reviewerId);
        service.setState(ServiceStateEnum.COMPLETED);

        ScheduleAppointment scheduleAppointment = new ScheduleAppointment();
        scheduleAppointment.setState(ScheduleStateEnum.COMPLETED);
        scheduleAppointment.setDateFinish(LocalDateTime.now().minusMonths(1));

        Review review = new Review();
        review.setReviewerId(reviewerId);
        review.setReviewedId(reviewedId);
        review.setServiceId(serviceId);
        review.setClassification(5);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(scheduleAppointmentRepository.findByServiceId(serviceId)).thenReturn(List.of(scheduleAppointment));
        when(userService.getUserById(reviewerId)).thenReturn(reviewer);
        when(userService.getUserById(reviewedId)).thenReturn(reviewed);
        when(reviewRepository.findReviewsByServiceId(serviceId)).thenReturn(List.of());
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = reviewService.createReview(review);

        assertEquals(200, response.getStatusCodeValue()); // Ensure the response is OK
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());

        Review savedReview = captor.getValue();
        assertEquals(reviewerId, savedReview.getReviewerId());
        assertEquals(reviewedId, savedReview.getReviewedId());
        assertEquals(serviceId, savedReview.getServiceId());
        assertEquals(5, savedReview.getClassification());
    }
}
