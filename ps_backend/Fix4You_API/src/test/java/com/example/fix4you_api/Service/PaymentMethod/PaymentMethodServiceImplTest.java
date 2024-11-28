package com.example.fix4you_api.Service.PaymentMethod;

import com.example.fix4you_api.Data.Models.PaymentMethod;
import com.example.fix4you_api.Data.MongoRepositories.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentMethodServiceImplTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentMethodServiceImpl paymentMethodService;

    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentMethod = new PaymentMethod();
        paymentMethod.setId("1");
        paymentMethod.setName("Credit Card");
    }

    @Test
    void testGetAllPaymentMethods() {
        when(paymentMethodRepository.findAll()).thenReturn(List.of(paymentMethod));

        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods();

        assertNotNull(paymentMethods);
        assertFalse(paymentMethods.isEmpty());
        assertEquals(1, paymentMethods.size());
        assertEquals("Credit Card", paymentMethods.get(0).getName());
        verify(paymentMethodRepository, times(1)).findAll();
    }

    @Test
    void testCreatePaymentMethod() {
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethod createdPaymentMethod = paymentMethodService.createPaymentMethod(paymentMethod);

        assertNotNull(createdPaymentMethod);
        assertEquals("Credit Card", createdPaymentMethod.getName());
        verify(paymentMethodRepository, times(1)).save(paymentMethod);
    }

    @Test
    void testUpdatePaymentMethod() {
        PaymentMethod updatedPaymentMethod = new PaymentMethod();
        updatedPaymentMethod.setName("Debit Card");

        when(paymentMethodRepository.findById("1")).thenReturn(Optional.of(paymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethod result = paymentMethodService.updatePaymentMethod("1", updatedPaymentMethod);

        assertEquals("Debit Card", result.getName());
        verify(paymentMethodRepository, times(1)).save(paymentMethod);
    }

    @Test
    void testPartialUpdatePaymentMethod() {
        Map<String, Object> updates = Map.of("name", "PayPal");

        when(paymentMethodRepository.findById("1")).thenReturn(Optional.of(paymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethod result = paymentMethodService.partialUpdatePaymentMethod("1", updates);

        assertEquals("PayPal", result.getName());
        verify(paymentMethodRepository, times(1)).save(paymentMethod);
    }

    @Test
    void testPartialUpdatePaymentMethod_InvalidField() {
        Map<String, Object> updates = Map.of("invalidField", "Some Value");

        when(paymentMethodRepository.findById("1")).thenReturn(Optional.of(paymentMethod));

        assertThrows(RuntimeException.class, () -> paymentMethodService.partialUpdatePaymentMethod("1", updates));
    }

    @Test
    void testDeletePaymentMethod() {
        when(paymentMethodRepository.findById("1")).thenReturn(Optional.of(paymentMethod));

        paymentMethodService.deletePaymentMethod("1");

        verify(paymentMethodRepository, times(1)).deleteById("1");
    }

    @Test
    void testFindOrThrow_PaymentMethodNotFound() {
        when(paymentMethodRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> paymentMethodService.updatePaymentMethod("1", paymentMethod));
    }

    @Test
    void testNameExists_True() {
        when(paymentMethodRepository.findAll()).thenReturn(List.of(paymentMethod));

        boolean result = paymentMethodService.nameExists("Credit Card");

        assertTrue(result);
    }

    @Test
    void testNameExists_False() {
        when(paymentMethodRepository.findAll()).thenReturn(List.of(paymentMethod));

        boolean result = paymentMethodService.nameExists("Cash");

        assertFalse(result);
    }
}
