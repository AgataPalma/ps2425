package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByProfessionalId(String professionalId);
}