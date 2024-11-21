package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.GoogleTokenUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GoogleTokenUserRepository extends MongoRepository<GoogleTokenUser, String> {
    GoogleTokenUser findByUserId(String userId);
    GoogleTokenUser findByEmail(String email);
}
