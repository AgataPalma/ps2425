package com.example.fix4you_api.Service.Ticket;

import com.example.fix4you_api.Data.Models.Dtos.CreateTicketRequestDTO;
import com.example.fix4you_api.Data.Models.Ticket;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Map;

public interface TicketService {
    List<Ticket> getAllTickets();
    List<Ticket> getTicketsByAdminId(String adminId);
    Ticket createTicket(CreateTicketRequestDTO createTicketRequest) throws MessagingException;
    Ticket updateTicket(String id, Ticket ticket);
    Ticket partialUpdateTicket(String id, Map<String, Object> updates);
    void deleteTicket(String id);
    void deleteTicketsForUser(String userId);
}