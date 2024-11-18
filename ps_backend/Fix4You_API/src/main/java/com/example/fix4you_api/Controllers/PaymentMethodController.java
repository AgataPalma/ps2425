package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.PaymentMethod;
import com.example.fix4you_api.Service.PaymentMethod.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paymentMethods")
@RequiredArgsConstructor
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public ResponseEntity<List<PaymentMethod>> getAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        if(paymentMethodService.nameExists(paymentMethod.getName())){
            return new ResponseEntity<>("PaymentMethod already exists", HttpStatus.CONFLICT);
        }

        PaymentMethod createdPaymentMethod = paymentMethodService.createPaymentMethod(paymentMethod);
        return new ResponseEntity<>(createdPaymentMethod, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@PathVariable String id, @RequestBody PaymentMethod paymentMethod) {
        PaymentMethod updatedPaymentMethod = paymentMethodService.updatePaymentMethod(id, paymentMethod);
        return new ResponseEntity<>(updatedPaymentMethod, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PaymentMethod> partialUpdatePaymentMethod(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        PaymentMethod updatedPaymentMethod = paymentMethodService.partialUpdatePaymentMethod(id, updates);
        return new ResponseEntity<>(updatedPaymentMethod, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable String id) {
        paymentMethodService.deletePaymentMethod(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}