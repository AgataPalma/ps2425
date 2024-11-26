package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.TicketStatusEnum;
import com.example.fix4you_api.Data.Models.CreateTicketRequest;
import com.example.fix4you_api.Data.Models.Dtos.SimpleUserDTO;
import com.example.fix4you_api.Data.Models.Ticket;
import com.example.fix4you_api.Service.Ticket.TicketService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createTicket(
            @RequestBody CreateTicketRequest createTicketRequest) throws MessagingException {
        Ticket createdTicket = ticketService.createTicket(createTicketRequest.getTicket(), createTicketRequest.getDescription());
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable String id, @RequestBody Ticket ticket) {
        Ticket updatedTicket = ticketService.updateTicket(id, ticket);
        return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ticket> partialUpdateTicket(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Ticket updatedTicket = ticketService.partialUpdateTicket(id, updates);
        return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
    }

    @PatchMapping("/accept/{id}")
    public ResponseEntity<?> acceptTicketByAdmin(@PathVariable("id") String id, @RequestBody SimpleUserDTO admin) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("admin", admin);
        updates.put("status", TicketStatusEnum.IN_REVIEW);

        return partialUpdateTicket(id, updates);
    }

    @PatchMapping("/resolve/{id}")
    public ResponseEntity<?> resolveTicket(@PathVariable("id") String id) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", TicketStatusEnum.RESOLVED);
        updates.put("ticketCloseDate", LocalDateTime.now());

        return partialUpdateTicket(id, updates);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        ticketService.deleteTicket(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}