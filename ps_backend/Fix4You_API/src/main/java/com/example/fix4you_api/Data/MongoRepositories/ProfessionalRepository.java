package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Professional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfessionalRepository extends MongoRepository<Professional, String> {
    List<Professional> findByUserType(EnumUserType userType);
    @Query(value = "{ 'languages': { $elemMatch: { id: ?0 } } }")
    List<Professional> findByLanguages_Id(String languagesId);
    @Query(value = "{ 'acceptedPayments': { $elemMatch: { id: ?0 } } }")
    List<Professional> findByAcceptedPayments_Id(String acceptedPaymentsId);
    @Query("{ 'userType' : ?0, '$or': [{'IsDeleted': false}, {'IsDeleted': null}] }")
    List<Professional> findActiveProfessionalsByUserType(EnumUserType userType);
    Professional findByEmail(String email);
    Optional<Professional> findById(String id);
}