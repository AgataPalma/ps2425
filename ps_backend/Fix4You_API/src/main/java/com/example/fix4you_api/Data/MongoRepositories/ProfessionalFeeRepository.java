package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProfessionalFeeRepository extends MongoRepository<ProfessionalsFee, String> {
    List<ProfessionalsFee> findByProfessionalId(String id);

    void deleteByProfessionalId(String professionalId);
}
