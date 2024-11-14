package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Professional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProfessionalRepository extends MongoRepository<Professional, String> {
    List<Professional> findByUserType(EnumUserType userType);
    @Query("{ 'userType' : ?0, '$or': [{'IsDeleted': false}, {'IsDeleted': null}] }")
    List<Professional> findActiveProfessionalsByUserType(EnumUserType userType);

    Professional findByEmail(String email);
}
