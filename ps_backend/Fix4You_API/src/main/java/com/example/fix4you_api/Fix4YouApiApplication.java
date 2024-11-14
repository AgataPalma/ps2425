package com.example.fix4you_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
public class Fix4YouApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Fix4YouApiApplication.class, args);
    }

}
