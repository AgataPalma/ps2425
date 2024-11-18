package com.example.fix4you_api.Service.PaymentMethod;

import com.example.fix4you_api.Data.Models.PaymentMethod;

import java.util.List;
import java.util.Map;

public interface PaymentMethodService {
    List<PaymentMethod> getAllPaymentMethods();
    PaymentMethod createPaymentMethod(PaymentMethod paymentMethod);
    PaymentMethod updatePaymentMethod(String id, PaymentMethod paymentMethod);
    PaymentMethod partialUpdatePaymentMethod(String id, Map<String, Object> updates);
    void deletePaymentMethod(String id);
    boolean nameExists(String name);
}