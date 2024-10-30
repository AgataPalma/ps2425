package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    PasswordResetToken findByToken(String token);
}
