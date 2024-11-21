package com.example.fix4you_api.Testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.fix4you_api.Controllers.ScheduleAppointmentController;
import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@SpringBootTest
class ScheduleAppointmentControllerTest {

    @Mock
    private ScheduleAppointmentRepository scheduleAppointmentRepository;

    @InjectMocks
    private ScheduleAppointmentController scheduleAppointmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addScheduleAppointment_NoConflict_Success() {
        // Arrange
        ScheduleAppointment newAppointment = new ScheduleAppointment(
                "1",
                "673bd353cba2fb7f65974d93",
                "673c7d48a405320502c8b672",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "673b99c5f648a87618f9ef48",
                ScheduleStateEnum.PENDING,
                false
        );

        when(scheduleAppointmentRepository.findByProfessionalId("673c7d48a405320502c8b672")).thenReturn(List.of());
        when(scheduleAppointmentRepository.save(newAppointment)).thenReturn(newAppointment);

        // Act
        ResponseEntity<String> response = (ResponseEntity<String>) scheduleAppointmentController.addScheduleAppointment(newAppointment);

        // Assert
        assertEquals("Schedule Appointment Added!", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(scheduleAppointmentRepository, times(1)).save(newAppointment);
    }

    @Test
    void addScheduleAppointment_WithConflict_Failure() {
        // Arrange
        ScheduleAppointment existingAppointment = new ScheduleAppointment(
                "1",
                "673bd353cba2fb7f65974d93",
                "673c7d48a405320502c8b672",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "673b99c5f648a87618f9ef48",
                ScheduleStateEnum.PENDING,
                false
        );

        ScheduleAppointment newAppointment = new ScheduleAppointment(
                "2",
                "673bd353cba2fb7f65974d93",
                "673c7d48a405320502c8b672",
                LocalDateTime.now().plusDays(1).plusHours(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                "673b99c5f648a87618f9ef48",
                ScheduleStateEnum.PENDING,
                false
        );

        when(scheduleAppointmentRepository.findByProfessionalId("673c7d48a405320502c8b672"))
                .thenReturn(List.of(existingAppointment));

        // Act
        ResponseEntity<String> response = (ResponseEntity<String>) scheduleAppointmentController.addScheduleAppointment(newAppointment);

        // Assert
        assertTrue(response.getBody().contains("Schedule appointment conflicted"));
        assertEquals(200, response.getStatusCodeValue());
        verify(scheduleAppointmentRepository, never()).save(any(ScheduleAppointment.class));
    }

    @Test
    void addScheduleAppointment_InvalidInput_Failure() {
        // Arrange
        ScheduleAppointment invalidAppointment = new ScheduleAppointment(
                null,
                null,
                null,
                null,
                LocalDateTime.now().plusDays(2),
                null,
                null,
                false
        );

        // Act
        ResponseEntity<String> response = (ResponseEntity<String>) scheduleAppointmentController.addScheduleAppointment(invalidAppointment);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCodeValue());
        verify(scheduleAppointmentRepository, never()).save(any(ScheduleAppointment.class));
    }

    @Test
    void getScheduleAppointment_Success() {
        // Arrange: Mock repository to return a list of schedule appointments
        List<ScheduleAppointment> mockAppointments = List.of(
                new ScheduleAppointment("1", "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().minusDays(1), LocalDateTime.now(), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false),
                new ScheduleAppointment("2", "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.COMPLETED, true)
        );

        when(scheduleAppointmentRepository.findAll()).thenReturn(mockAppointments);

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getAllScheduleAppointments();

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ScheduleAppointment> responseBody = (List<ScheduleAppointment>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals("673bd353cba2fb7f65974d93", responseBody.get(0).getServiceId());
        assertEquals("673bd353cba2fb7f65974d93", responseBody.get(1).getServiceId());
    }

    @Test
    void getScheduleAppointment_EmptyList() {
        // Arrange: Mock repository to return an empty list
        when(scheduleAppointmentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getAllScheduleAppointments();

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ScheduleAppointment> responseBody = (List<ScheduleAppointment>) response.getBody();
        assertTrue(responseBody.isEmpty());
    }

    @Test
    void getProfessionalScheduleAppointments_Success() {
        // Arrange: Mock repository to return schedule appointments for a professional
        String professionalId = "673c7d48a405320502c8b672";
        List<ScheduleAppointment> mockAppointments = List.of(
                new ScheduleAppointment("1", "673bd353cba2fb7f65974d93", professionalId, LocalDateTime.now().minusDays(1), LocalDateTime.now(), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false),
                new ScheduleAppointment("2", "673bd353cba2fb7f65974d93", professionalId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.COMPLETED, true)
        );
        when(scheduleAppointmentRepository.findByProfessionalId(professionalId)).thenReturn(mockAppointments);

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getProfessionalScheduleAppointments(professionalId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ScheduleAppointment> responseBody = (List<ScheduleAppointment>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals("673bd353cba2fb7f65974d93", responseBody.get(0).getServiceId());
        assertEquals("673bd353cba2fb7f65974d93", responseBody.get(1).getServiceId());
    }

    @Test
    void getProfessionalScheduleAppointments_EmptyList() {
        // Arrange: Mock repository to return an empty list
        String professionalId = "673c7d48a405320502c8b672";
        when(scheduleAppointmentRepository.findByProfessionalId(professionalId)).thenReturn(Collections.emptyList());

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getProfessionalScheduleAppointments(professionalId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ScheduleAppointment> responseBody = (List<ScheduleAppointment>) response.getBody();
        assertTrue(responseBody.isEmpty());
    }

    @Test
    void getProfessionalScheduleAppointments_ExceptionThrown() {
        // Arrange: Mock repository to throw an exception
        String professionalId = "673c7d48a405320502c8b672";
        when(scheduleAppointmentRepository.findByProfessionalId(professionalId)).thenThrow(new RuntimeException("Database error"));

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getProfessionalScheduleAppointments(professionalId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

    @Test
    void getClientScheduleAppointments_Success() {
        // Arrange: Mock repository to return schedule appointments for a client
        String clientId = "673b99c5f648a87618f9ef48";
        List<ScheduleAppointment> mockAppointments = List.of(
                new ScheduleAppointment("1", "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().minusDays(1), LocalDateTime.now(), clientId, ScheduleStateEnum.PENDING, false),
                new ScheduleAppointment("2", "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), clientId, ScheduleStateEnum.COMPLETED, true)
        );
        when(scheduleAppointmentRepository.findByClientId(clientId)).thenReturn(mockAppointments);

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getClientScheduleAppointments(clientId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ScheduleAppointment> responseBody = (List<ScheduleAppointment>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals("673bd353cba2fb7f65974d93", responseBody.get(0).getServiceId());
        assertEquals("673bd353cba2fb7f65974d93", responseBody.get(1).getServiceId());
    }

    @Test
    void getClientScheduleAppointments_EmptyList() {
        // Arrange: Mock repository to return an empty list
        String clientId = "673b99c5f648a87618f9ef48";
        when(scheduleAppointmentRepository.findByClientId(clientId)).thenReturn(Collections.emptyList());

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getClientScheduleAppointments(clientId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ScheduleAppointment> responseBody = (List<ScheduleAppointment>) response.getBody();
        assertTrue(responseBody.isEmpty());
    }

    @Test
    void getClientScheduleAppointments_ExceptionThrown() {
        // Arrange: Mock repository to throw an exception
        String clientId = "673b99c5f648a87618f9ef48";
        when(scheduleAppointmentRepository.findByClientId(clientId)).thenThrow(new RuntimeException("Database error"));

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getClientScheduleAppointments(clientId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

    @Test
    void approveScheduleAppointment_Success() {
        // Arrange: Mock repository to return an existing appointment
        String appointmentId = "1";
        ScheduleAppointment mockAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false
        );
        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.approveScheduleAppointment(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ScheduleAppointment responseBody = ((Optional<ScheduleAppointment>) response.getBody()).get();
        assertEquals(ScheduleStateEnum.CONFIRMED, responseBody.getState());

        // Verify that the save method was called
        verify(scheduleAppointmentRepository, times(1)).save(mockAppointment);
    }

    @Test
    void approveScheduleAppointment_NotFound() {
        // Arrange: Mock repository to return an empty Optional
        String appointmentId = "1";
        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.approveScheduleAppointment(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No value present")); // From Optional.get() exception
    }

    @Test
    void approveScheduleAppointment_ExceptionThrown() {
        // Arrange: Mock repository to throw an exception
        String appointmentId = "1";
        when(scheduleAppointmentRepository.findById(appointmentId)).thenThrow(new RuntimeException("Database error"));

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.approveScheduleAppointment(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

    @Test
    void disapproveScheduleAppointment_Success() {
        // Arrange: Mock repository to return an existing appointment
        String appointmentId = "1";
        ScheduleAppointment mockAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false
        );
        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.disapproveScheduleAppointment(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ScheduleAppointment responseBody = ((Optional<ScheduleAppointment>) response.getBody()).get();
        assertEquals(ScheduleStateEnum.CANCELED, responseBody.getState());

        // Verify that the save method was called
        verify(scheduleAppointmentRepository, times(1)).save(mockAppointment);
    }

    @Test
    void disapproveScheduleAppointment_NotFound() {
        // Arrange: Mock repository to return an empty Optional
        String appointmentId = "1";
        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.disapproveScheduleAppointment(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No value present")); // From Optional.get() exception
    }

    @Test
    void disapproveScheduleAppointment_ExceptionThrown() {
        // Arrange: Mock repository to throw an exception
        String appointmentId = "1";
        when(scheduleAppointmentRepository.findById(appointmentId)).thenThrow(new RuntimeException("Database error"));

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.disapproveScheduleAppointment(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

    @Test
    void getScheduleAppointment_Found() {
        // Arrange: Mock repository to return a schedule appointment
        String appointmentId = "1";
        ScheduleAppointment mockAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false
        );
        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getScheduleAppointmentById(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockAppointment, response.getBody());
    }

    @Test
    void getScheduleAppointment_NotFound() {
        // Arrange: Mock repository to return an empty Optional
        String appointmentId = "1";
        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getScheduleAppointmentById(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Couldn't find any schedule appointment with the id: '1'!", response.getBody());
    }

    @Test
    void getScheduleAppointment_ExceptionThrown() {
        // Arrange: Mock repository to throw an exception
        String appointmentId = "1";
        when(scheduleAppointmentRepository.findById(appointmentId)).thenThrow(new RuntimeException("Database error"));

        // Act: Call the controller method
        ResponseEntity<?> response = scheduleAppointmentController.getScheduleAppointmentById(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

    @Test
    void updateScheduleAppointment_Success() {
        // Arrange
        String appointmentId = "1";
        ScheduleAppointment existingAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false
        );

        ScheduleAppointment updatedAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4), "673b99c5f648a87618f9ef48", ScheduleStateEnum.CONFIRMED, false
        );

        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(scheduleAppointmentRepository.findByProfessionalId("673c7d48a405320502c8b672"))
                .thenReturn(List.of(existingAppointment));

        // Act
        ResponseEntity<?> response = scheduleAppointmentController.updateScheduleAppointment(appointmentId, updatedAppointment);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedAppointment, response.getBody());
        verify(scheduleAppointmentRepository).save(updatedAppointment);
    }

    @Test
    void updateScheduleAppointment_Conflict() {
        // Arrange
        String appointmentId = "1";
        ScheduleAppointment existingAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false
        );

        ScheduleAppointment conflictingAppointment = new ScheduleAppointment(
                "2", "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3), "673b99c5f648a87618f9ef48", ScheduleStateEnum.CONFIRMED, false
        );

        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(scheduleAppointmentRepository.findByProfessionalId("673c7d48a405320502c8b672"))
                .thenReturn(List.of(existingAppointment, conflictingAppointment));

        // Act
        ResponseEntity<?> response = scheduleAppointmentController.updateScheduleAppointment(appointmentId, conflictingAppointment);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Schedule appointment conflicted"));
        verify(scheduleAppointmentRepository, never()).save(conflictingAppointment);
    }

    @Test
    void partialUpdateScheduleAppointment_Success() {
        // Arrange
        String appointmentId = "1";
        ScheduleAppointment existingAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false
        );

        Map<String, Object> updates = Map.of(
                "state", ScheduleStateEnum.CONFIRMED,
                "clientId", "updatedClient"
        );

        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // Act
        ResponseEntity<?> response = scheduleAppointmentController.partialUpdateScheduleAppointment(appointmentId, updates);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ScheduleStateEnum.CONFIRMED, ((ScheduleAppointment) response.getBody()).getState());
        assertEquals("updatedClient", ((ScheduleAppointment) response.getBody()).getClientId());
        verify(scheduleAppointmentRepository).save(any(ScheduleAppointment.class));
    }

    @Test
    void partialUpdateScheduleAppointment_Conflict() {
        // Arrange
        String appointmentId = "1";
        ScheduleAppointment existingAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false
        );

        Map<String, Object> updates = Map.of(
                "dateStart", LocalDateTime.now().plusDays(2).toString(),
                "dateFinish", LocalDateTime.now().plusDays(3).toString()
        );

        ScheduleAppointment conflictingAppointment = new ScheduleAppointment(
                "2", "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3), "673b99c5f648a87618f9ef48", ScheduleStateEnum.CONFIRMED, false
        );

        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(scheduleAppointmentRepository.findByProfessionalId("673c7d48a405320502c8b672"))
                .thenReturn(List.of(existingAppointment, conflictingAppointment));

        // Act
        ResponseEntity<?> response = scheduleAppointmentController.partialUpdateScheduleAppointment(appointmentId, updates);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Schedule appointment conflicted"));
        verify(scheduleAppointmentRepository, never()).save(existingAppointment);
    }

    @Test
    void deleteScheduleAppointment_Success() {
        // Arrange
        String appointmentId = "1";
        ScheduleAppointment existingAppointment = new ScheduleAppointment(
                appointmentId, "673bd353cba2fb7f65974d93", "673c7d48a405320502c8b672", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), "673b99c5f648a87618f9ef48", ScheduleStateEnum.PENDING, false
        );

        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // Act
        ResponseEntity<?> response = scheduleAppointmentController.deleteScheduleAppointment(appointmentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("was deleted"));
        verify(scheduleAppointmentRepository).deleteById(appointmentId);
    }

    @Test
    void deleteScheduleAppointment_NotFound() {
        // Arrange
        String appointmentId = "10";
        when(scheduleAppointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = scheduleAppointmentController.deleteScheduleAppointment(appointmentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Couldn't find any schedule appointment"));
        verify(scheduleAppointmentRepository, never()).deleteById(appointmentId);
    }

    @Test
    void deleteScheduleAppointment_Exception() {
        // Arrange
        String appointmentId = "1";
        doThrow(new RuntimeException("Database error")).when(scheduleAppointmentRepository).deleteById(appointmentId);

        // Act
        ResponseEntity<?> response = scheduleAppointmentController.deleteScheduleAppointment(appointmentId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("There was an error trying to delete"));
        verify(scheduleAppointmentRepository).deleteById(appointmentId);
    }

}

