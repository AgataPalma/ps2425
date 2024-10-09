package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
