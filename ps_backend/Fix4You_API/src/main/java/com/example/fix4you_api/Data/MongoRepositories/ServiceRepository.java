package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Service;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ServiceRepository extends MongoRepository<Service, String> {
    List<Service> findByProfessionalId(String id);

    List<Service> findByClientId(String id);

    List<Service> findByProfessionalIdAndState(String professionalId, ServiceStateEnum state);

    List<Service> findByUrgentTrueAndState(ServiceStateEnum state);

    List<Service> findByCategoryAndState(String name, ServiceStateEnum state);

    @Query( "SELECT s.clientId" +
            "COUNT(s) " +
            "FROM Service s " +
            "WHERE s.clientId IS NOT NULL " +
            "WHERE s.state IS COMPLETED" +
            "GROUP BY s.clientId " +
            "ORDER BY COUNT(s) DESC")
    List<Service> findTop10ClientsWithMostServices();

    @Query( "SELECT s.professionalId" +
            "COUNT(s) " +
            "FROM Service s " +
            "WHERE s.professionalId IS NOT NULL" +
            "WHERE s.state IS COMPLETED" +
            "GROUP BY s.professionalId " +
            "ORDER BY COUNT(s) DESC")
    List<Service> findTop10ProfessionalsWithMostServices();

    @Query( "SELECT s.clientId " +
            "SUM(s.price) as totalSpent " +
            "FROM Service s " +
            "WHERE s.clientId IS NOT NULL " +
            "WHERE s.state IS COMPLETED" +
            "GROUP BY s.clientId " +
            "ORDER BY totalSpent DESC")
    List<Service> findTopClientsByTotalSpending();

    boolean existsById(String id);
}
