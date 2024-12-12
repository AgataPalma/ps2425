package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    void deleteByUser_Id(String userId);
    List<Ticket> findByAdmin_Id(String adminId);
}
