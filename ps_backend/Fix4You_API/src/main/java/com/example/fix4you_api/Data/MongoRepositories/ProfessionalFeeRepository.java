package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.example.fix4you_api.Data.Models.Service;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProfessionalFeeRepository extends MongoRepository<ProfessionalsFee, String> {
    List<ProfessionalsFee> findByProfessionalId(String id);

    @Query( "SELECT pf.professional.id " +
            "SUM(pf.value) as totalValue " +
            "FROM ProfessionalsFee pf " +
            "WHERE pf.professional.id IS NOT NULL " +
            "GROUP BY s.professional.id " +
            "ORDER BY totalValue DESC")
    List<ProfessionalsFee> findTopProfessionalsByTotalSpending();

    void deleteByProfessionalId(String professionalId);
}
