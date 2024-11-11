package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Service;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServiceRepository extends MongoRepository<Service, String> {
    List<Service> findByProfessionalId(String id);

    List<Service> findByClientId(String id);

    boolean existsById(String id);
}
