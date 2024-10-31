package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProfessionalRepository extends MongoRepository<Professional, String> {
    List<Professional> findByUserType(EnumUserType userType);
    Professional findByEmail(String email);
}
