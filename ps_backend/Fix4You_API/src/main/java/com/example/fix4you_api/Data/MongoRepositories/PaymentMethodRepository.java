package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.PaymentMethod;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentMethodRepository extends MongoRepository<PaymentMethod, String> {
}