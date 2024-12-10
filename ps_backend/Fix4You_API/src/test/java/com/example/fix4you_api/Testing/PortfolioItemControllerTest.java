package com.example.fix4you_api.Testing;

import com.example.fix4you_api.Controllers.PortfolioItemController;
import com.example.fix4you_api.Controllers.PortfolioItemController;
import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.PortfolioItem;
import com.example.fix4you_api.Data.Models.PortfolioItem;
import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PortfolioItemControllerTest {
    @Mock
    private PortfolioItemRepository portfolioItemRepository;

    @InjectMocks
    private PortfolioItemController portfolioItemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPortfolioItem_NoConflict_Success() {
        // Arrange
        PortfolioItem newPortfolioItem = new PortfolioItem(
                "1",
                "673bd353cba2fb7f65974d93",
                null,
                "description"
        );

        when(portfolioItemRepository.findByProfessionalId("673c7d48a405320502c8b672")).thenReturn(List.of());
        when(portfolioItemRepository.save(newPortfolioItem)).thenReturn(newPortfolioItem);

        // Act
        ResponseEntity<String> response = portfolioItemController.addPortfolioItem(newPortfolioItem);

        // Assert
        assertEquals("O item do portefólio foi adicionado com sucesso!", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(portfolioItemRepository, times(1)).save(newPortfolioItem);
    }

    @Test
    void addPortfolioItem_InvalidInput_Failure() {
        // Arrange
        PortfolioItem invalidPortfolioItem = new PortfolioItem(
                null,
                null,
                null,
                null
        );

        // Act
        ResponseEntity<String> response = portfolioItemController.addPortfolioItem(invalidPortfolioItem);

        // Assert
        assertNull(invalidPortfolioItem.getProfessionalId());
        assertNull(invalidPortfolioItem.getDescription());
    }

    @Test
    void getPortfolioItem_Success() {
        // Arrange: Mock repository to return a list of schedule appointments
        List<PortfolioItem> mockAppointments = List.of(
                new PortfolioItem(
                        "1",
                        "673bd353cba2fb7f65974d93",
                        null,
                        "description"
                ),
                new PortfolioItem(
                        "2",
                        "673bd353cba2fb7f65974d93",
                        null,
                        "description"
                )
        );

        when(portfolioItemRepository.findAll()).thenReturn(mockAppointments);

        // Act: Call the controller method
        ResponseEntity<?> response = portfolioItemController.getPortfolioItem();

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PortfolioItem> responseBody = (List<PortfolioItem>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals("1", responseBody.get(0).getId());
        assertEquals("2", responseBody.get(1).getId());
    }

    @Test
    void getPortfolioItem_EmptyList() {
        // Arrange: Mock repository to return an empty list
        when(portfolioItemRepository.findAll()).thenReturn(Collections.emptyList());

        // Act: Call the controller method
        ResponseEntity<?> response = portfolioItemController.getPortfolioItem();

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PortfolioItem> responseBody = (List<PortfolioItem>) response.getBody();
        assertTrue(responseBody.isEmpty());
    }

    @Test
    void getProfessionalPortfolioItems_Success() {
        // Arrange: Mock repository to return schedule appointments for a professional
        String professionalId = "673c7d48a405320502c8b672";
        List<PortfolioItem> mockAppointments = List.of(
                new PortfolioItem(
                        "1",
                        "673bd353cba2fb7f65974d93",
                        null,
                        "description"
                ),
                new PortfolioItem(
                        "2",
                        "673bd353cba2fb7f65974d93",
                        null,
                        "description"
                )
        );
        when(portfolioItemRepository.findByProfessionalId(professionalId)).thenReturn(mockAppointments);

        // Act: Call the controller method
        ResponseEntity<?> response = portfolioItemController.getUserPortfolioItem(professionalId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PortfolioItem> responseBody = (List<PortfolioItem>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals("1", responseBody.get(0).getId());
        assertEquals("2", responseBody.get(1).getId());
    }

    @Test
    void getProfessionalPortfolioItems_EmptyList() {
        // Arrange: Mock repository to return an empty list
        String professionalId = "673c7d48a405320502c8b672";
        when(portfolioItemRepository.findByProfessionalId(professionalId)).thenReturn(Collections.emptyList());

        // Act: Call the controller method
        ResponseEntity<?> response = portfolioItemController.getUserPortfolioItem(professionalId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PortfolioItem> responseBody = (List<PortfolioItem>) response.getBody();
        assertTrue(responseBody.isEmpty());
    }

    @Test
    void getProfessionalPortfolioItems_ExceptionThrown() {
        // Arrange: Mock repository to throw an exception
        String professionalId = "673c7d48a405320502c8b672";
        when(portfolioItemRepository.findByProfessionalId(professionalId)).thenThrow(new RuntimeException("Database error"));

        // Act: Call the controller method
        ResponseEntity<?> response = portfolioItemController.getUserPortfolioItem(professionalId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

    @Test
    void getPortfolioItem_Found() {
        // Arrange: Mock repository to return a schedule appointment
        String appointmentId = "1";
        PortfolioItem mockAppointment = new PortfolioItem(
                "1",
                "673bd353cba2fb7f65974d93",
                null,
                "description"
        );
        when(portfolioItemRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act: Call the controller method
        ResponseEntity<?> response = portfolioItemController.getPortfolioItem(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockAppointment, response.getBody());
    }

    @Test
    void getPortfolioItem_NotFound() {
        // Arrange: Mock repository to return an empty Optional
        String appointmentId = "1000";
        when(portfolioItemRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act: Call the controller method
        ResponseEntity<?> response = portfolioItemController.deletePortfolioItem(appointmentId);

        // Assert: Verify the response
        assertTrue(response.getBody().toString().contains("Não foi possível encontrar nenhum item do portefólio"));
    }

    @Test
    void getPortfolioItem_ExceptionThrown() {
        // Arrange: Mock repository to throw an exception
        String appointmentId = "1";
        when(portfolioItemRepository.findById(appointmentId)).thenThrow(new RuntimeException("Database error"));

        // Act: Call the controller method
        ResponseEntity<?> response = portfolioItemController.getPortfolioItem(appointmentId);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

    @Test
    void updatePortfolioItem_Success() {
        // Arrange
        String appointmentId = "1";
        PortfolioItem existingPortfolioItem = new PortfolioItem(
                "1",
                "673bd353cba2fb7f65974d93",
                null,
                "description"
        );

        PortfolioItem updatedPortfolioItem = new PortfolioItem(
                "1",
                "673bd353cba2fb7f65974d93",
                null,
                "description"
        );

        when(portfolioItemRepository.findById(appointmentId)).thenReturn(Optional.of(existingPortfolioItem));
        when(portfolioItemRepository.findByProfessionalId("673c7d48a405320502c8b672"))
                .thenReturn(List.of(existingPortfolioItem));

        // Act
        ResponseEntity<?> response = portfolioItemController.updatePortfolioItem(appointmentId, updatedPortfolioItem);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(portfolioItemRepository).save(updatedPortfolioItem);
    }

    @Test
    void partialUpdatePortfolioItem_Success() {
        // Arrange
        String appointmentId = "1";
        PortfolioItem existingPortfolioItem = new PortfolioItem(
                "1",
                "673bd353cba2fb7f65974d93",
                null,
                "updatedDescription"
        );

        Map<String, Object> updates = Map.of(
                "description", "description"
        );

        when(portfolioItemRepository.findById(appointmentId)).thenReturn(Optional.of(existingPortfolioItem));

        // Act
        ResponseEntity<?> response = portfolioItemController.partialUpdatePortfolioItem(appointmentId, updates);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(portfolioItemRepository).save(any(PortfolioItem.class));
    }

    @Test
    void deletePortfolioItem_Success() {
        // Arrange
        String appointmentId = "1";
        PortfolioItem existingPortfolioItem = new PortfolioItem(
                "1",
                "673bd353cba2fb7f65974d93",
                null,
                "updatedDescription"
        );

        when(portfolioItemRepository.findById(appointmentId)).thenReturn(Optional.of(existingPortfolioItem));

        // Act
        ResponseEntity<?> response = portfolioItemController.deletePortfolioItem(appointmentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("foi eliminad"));
        verify(portfolioItemRepository).deleteById(appointmentId);
    }

    @Test
    void deletePortfolioItem_NotFound() {
        // Arrange
        String appointmentId = "100";
        when(portfolioItemRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = portfolioItemController.deletePortfolioItem(appointmentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Não foi possível encontrar nenhum item do portefólio"));
    }

    @Test
    void deletePortfolioItem_Exception() {
        // Arrange
        String appointmentId = "1";
        doThrow(new RuntimeException("Database error")).when(portfolioItemRepository).deleteById(appointmentId);

        // Act
        ResponseEntity<?> response = portfolioItemController.deletePortfolioItem(appointmentId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Ocorreu um erro ao tentar eliminar o item do portefólio com o id: '" + appointmentId + "'!"));
        verify(portfolioItemRepository).deleteById(appointmentId);
    }

}
