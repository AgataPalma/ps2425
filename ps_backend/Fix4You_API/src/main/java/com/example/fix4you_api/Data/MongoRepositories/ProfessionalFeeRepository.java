package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProfessionalFeeRepository extends MongoRepository<ProfessionalsFee, String> {
    List<ProfessionalsFee> findByProfessional_Id(String id);
    List<ProfessionalsFee> findByProfessional_IdAndPaymentStatus(String professionalId, PaymentStatusEnum paymentStatusEnum);

    @Query( "SELECT pf.professional.id " +
            "SUM(pf.value) as totalValue " +
            "FROM ProfessionalsFee pf " +
            "WHERE pf.professional.id IS NOT NULL " +
            "GROUP BY s.professional.id " +
            "ORDER BY totalValue DESC")
    List<ProfessionalsFee> findTopProfessionalsByTotalSpending();

    void deleteByProfessional_Id(String professionalId);
}
