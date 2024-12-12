package com.example.fix4you_api.Controller;

import com.example.fix4you_api.Controllers.ProfessionalFeeController;
import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Models.Dtos.ProfessionalsFeeSaveDTO;
import com.example.fix4you_api.Data.Models.Dtos.SimpleProfessionalDTO;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ProfessionalsFee.ProfessionalsFeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProfessionalsFeeControllerTest {

    @Mock
    private ProfessionalsFeeService professionalsFeeService;

    @Mock
    private ProfessionalService professionalService;

    @InjectMocks
    private ProfessionalFeeController professionalFeeController;

    private ProfessionalsFee mockProfessionalsFee;

    private ProfessionalsFeeSaveDTO mockProfessionalsFeeDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SimpleProfessionalDTO mockProfessional = new SimpleProfessionalDTO();
        mockProfessional.setId("prof123");
        mockProfessional.setEmail("test@example.com");
        mockProfessional.setName("John Doe");
        mockProfessional.setNif("123456789");

        mockProfessionalsFee = new ProfessionalsFee();
        mockProfessionalsFee.setId("fee123");
        mockProfessionalsFee.setProfessional(mockProfessional);
        mockProfessionalsFee.setValue(20.0f);
        mockProfessionalsFee.setNumberServices(3);
        mockProfessionalsFee.setRelatedMonthYear("11-2024");
        mockProfessionalsFee.setPaymentDate(LocalDateTime.now());
        mockProfessionalsFee.setPaymentStatus(PaymentStatusEnum.PENDING);

        mockProfessionalsFeeDto = new ProfessionalsFeeSaveDTO();
        mockProfessionalsFeeDto.setId("fee123");
        mockProfessionalsFeeDto.setProfessional(mockProfessional);
        mockProfessionalsFeeDto.setValue(20.0f);
        mockProfessionalsFeeDto.setNumberServices(3);
        mockProfessionalsFeeDto.setRelatedMonthYear("11-2024");
        mockProfessionalsFeeDto.setPaymentDate(LocalDateTime.now());
        mockProfessionalsFeeDto.setPaymentStatus(PaymentStatusEnum.PENDING);
    }

    /*
    @Test
    void testCreateProfessionalFee() {
        when(professionalsFeeService.createProfessionalsFee(any(ProfessionalsFee.class))).thenReturn(mockProfessionalsFee);

        ResponseEntity<ProfessionalsFee> response = professionalFeeController.createProfessionalFee(mockProfessionalsFeeDto);

        verify(professionalsFeeService).createProfessionalsFee(mockProfessionalsFee);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockProfessionalsFee, response.getBody());
    }
    */

    @Test
    void testGetProfessionalFeeById() {
        String feeId = "fee123";
        when(professionalsFeeService.getProfessionalsFeeById(feeId)).thenReturn(mockProfessionalsFee);

        ResponseEntity<ProfessionalsFee> response = professionalFeeController.getProfessionalFee(feeId);

        verify(professionalsFeeService).getProfessionalsFeeById(feeId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProfessionalsFee, response.getBody());
    }

    @Test
    void testGetUserProfessionalFee() {
        String professionalId = "prof123";
        when(professionalsFeeService.getProfessionalsFeeForProfessionalId(professionalId)).thenReturn(List.of(mockProfessionalsFee));

        ResponseEntity<List<ProfessionalsFee>> response = professionalFeeController.getUserProfessionalFee(professionalId);

        verify(professionalsFeeService).getProfessionalsFeeForProfessionalId(professionalId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(mockProfessionalsFee, response.getBody().get(0));
    }

    /*
    @Test
    void testCreateProfessionalFee_InvalidInput() {
        when(professionalsFeeService.createProfessionalsFee(any(ProfessionalsFee.class)))
                .thenThrow(new IllegalArgumentException("Invalid fee data"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professionalFeeController.createProfessionalFee(new ProfessionalsFeeSaveDto())
        );

        verify(professionalsFeeService).createProfessionalsFee(any(ProfessionalsFee.class));
        assertEquals("Invalid fee data", exception.getMessage());
    }
    */

    @Test
    void testGetProfessionalFee_InvalidId() {
        String invalidId = "invalidFee";
        when(professionalsFeeService.getProfessionalsFeeById(invalidId))
                .thenThrow(new IllegalArgumentException("Fee not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professionalFeeController.getProfessionalFee(invalidId)
        );

        verify(professionalsFeeService).getProfessionalsFeeById(invalidId);
        assertEquals("Fee not found", exception.getMessage());
    }

    @Test
    void testDeleteProfessionalFee_InvalidId() {
        String invalidId = "invalidFee";
        doThrow(new IllegalArgumentException("Fee not found")).when(professionalsFeeService).deleteProfessionalFee(invalidId);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professionalFeeController.deleteProfessionalFee(invalidId)
        );

        verify(professionalsFeeService).deleteProfessionalFee(invalidId);
        assertEquals("Fee not found", exception.getMessage());
    }

    @Test
    void testSetFeeAsPaid_FeeNotFound() throws Exception {
        String invalidFeeId = "invalidFee";

        when(professionalsFeeService.setFeeAsPaid(invalidFeeId))
                .thenThrow(new IllegalArgumentException("Fee not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professionalFeeController.setFeeAsPaid(invalidFeeId)
        );

        verify(professionalsFeeService).setFeeAsPaid(invalidFeeId);
        assertEquals("Fee not found", exception.getMessage());
    }

}