package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Service;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServiceRepository extends MongoRepository<Service, String> {
    List<Service> findByProfessionalId(String id);

    List<Service> findByClientId(String id);

    List<Service> findByProfessionalIdAndState(String professionalId, ServiceStateEnum state);

    List<Service> findByUrgentTrueAndState(ServiceStateEnum state);

    boolean existsById(String id);
}
