package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketServiceRepository extends MongoRepository<Ticket, String> {
    void deleteByUserId(String userId);
}
