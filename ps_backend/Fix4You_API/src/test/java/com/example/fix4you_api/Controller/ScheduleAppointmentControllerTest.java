package com.example.fix4you_api.Controller;

import com.example.fix4you_api.Controllers.ScheduleAppointmentController;
import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleAppointmentControllerTest {

    @InjectMocks
    private ScheduleAppointmentController scheduleAppointmentController;

    @Mock
    private ScheduleAppointmentRepository scheduleAppointmentRepository;

    private ScheduleAppointment validAppointment;

    @BeforeEach
    void setUp() {
        validAppointment = new ScheduleAppointment();
        validAppointment.setId("1");
        validAppointment.setServiceId("service-teste");
        validAppointment.setClientId("client-test");
        validAppointment.setProfessionalId("professional-test");
        validAppointment.setDateStart(LocalDateTime.now().plusDays(1));
        validAppointment.setDateFinish(LocalDateTime.now().plusDays(2));
        validAppointment.setState(ScheduleStateEnum.PENDING);
    }

    @Test
    void testAddScheduleAppointment_DateStartInPast() {
        validAppointment.setDateStart(LocalDateTime.now().minusDays(1));

        ResponseEntity<?> response = scheduleAppointmentController.addScheduleAppointment(validAppointment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Start date must be in the future", response.getBody());
    }

    @Test
    void testAddScheduleAppointment_DateFinishInPast() {
        validAppointment.setDateFinish(LocalDateTime.now().minusDays(1));

        ResponseEntity<?> response = scheduleAppointmentController.addScheduleAppointment(validAppointment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Finish date must be in the future", response.getBody());
    }

    @Test
    void testAddScheduleAppointment_DateFinishBeforeDateStart() {
        validAppointment.setDateStart(LocalDateTime.now().plusDays(3));

        ResponseEntity<?> response = scheduleAppointmentController.addScheduleAppointment(validAppointment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Finish date must be after start date", response.getBody());
    }

    @Test
    void testAddScheduleAppointment_ValidData() {
        when(scheduleAppointmentRepository.save(validAppointment)).thenReturn(validAppointment);

        ResponseEntity<?> response = scheduleAppointmentController.addScheduleAppointment(validAppointment);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(validAppointment, response.getBody());
    }

}