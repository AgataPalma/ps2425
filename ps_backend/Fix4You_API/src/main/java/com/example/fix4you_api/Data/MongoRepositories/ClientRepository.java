package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<Client, String> {
}