package com.example.fix4you_api.Service.Ticket;

import com.example.fix4you_api.Data.Models.Ticket;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Map;

public interface TicketService {
    List<Ticket> getAllTickets();
    Ticket createTicket(Ticket ticket, String description) throws MessagingException;
    Ticket updateTicket(String id, Ticket ticket);
    Ticket partialUpdateTicket(String id, Map<String, Object> updates);
    void deleteTicket(String id);
    void deleteTicketsForUser(String userId);
}