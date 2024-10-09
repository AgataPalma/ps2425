package com.example.fix4you_api;

import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class Fix4YouApiApplication {

    private final UserRepository userRepository;

    public Fix4YouApiApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Fix4YouApiApplication.class, args);
    }

}
