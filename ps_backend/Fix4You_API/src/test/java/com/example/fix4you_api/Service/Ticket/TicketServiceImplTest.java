package com.example.fix4you_api.Service.Ticket;

import com.example.fix4you_api.Data.Enums.TicketStatusEnum;
import com.example.fix4you_api.Data.Models.Dtos.CreateTicketRequestDTO;
import com.example.fix4you_api.Data.Models.Dtos.SimpleUserDTO;
import com.example.fix4you_api.Data.Models.Ticket;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.TicketRepository;
import com.example.fix4you_api.Service.Email.EmailSenderService;
import com.example.fix4you_api.Service.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTickets() {
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> result = ticketService.getAllTickets();

        assertEquals(2, result.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void testCreateTicket() throws MessagingException {
        CreateTicketRequestDTO requestDTO = new CreateTicketRequestDTO();
        requestDTO.setUserId("123");
        requestDTO.setTitle("Test Ticket");
        requestDTO.setDescription("Test Description");

        User user = new User();
        user.setId("123");
        user.setEmail("test@example.com");
        when(userService.getUserById("123")).thenReturn(user);

        Ticket ticket = new Ticket();
        ticket.setUser(new SimpleUserDTO("123", "test@example.com"));
        ticket.setTitle("Test Ticket");
        ticket.setStatus(TicketStatusEnum.NEW);
        ticket.setTicketStartDate(LocalDateTime.now());

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket result = ticketService.createTicket(requestDTO);

        assertNotNull(result);
        assertEquals("Test Ticket", result.getTitle());
        verify(emailSenderService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testUpdateTicket() {
        Ticket existingTicket = new Ticket();
        existingTicket.setId("123");
        when(ticketRepository.findById("123")).thenReturn(Optional.of(existingTicket));

        Ticket updatedTicket = new Ticket();
        updatedTicket.setTitle("Updated Title");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(existingTicket);

        Ticket result = ticketService.updateTicket("123", updatedTicket);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(ticketRepository, times(1)).save(existingTicket);
    }

    @Test
    void testPartialUpdateTicket() {
        Ticket existingTicket = new Ticket();
        existingTicket.setId("123");
        when(ticketRepository.findById("123")).thenReturn(Optional.of(existingTicket));
        when(ticketRepository.save(any(Ticket.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Partially Updated Title");
        updates.put("status", "RESOLVED");

        Ticket result = ticketService.partialUpdateTicket("123", updates);

        assertNotNull(result);
        assertEquals("Partially Updated Title", result.getTitle());
        assertEquals(TicketStatusEnum.RESOLVED, result.getStatus());
        verify(ticketRepository, times(1)).save(existingTicket);
    }

    @Test
    void testDeleteTicket() {
        doNothing().when(ticketRepository).deleteById("123");

        ticketService.deleteTicket("123");

        verify(ticketRepository, times(1)).deleteById("123");
    }

    @Test
    void testDeleteTicketsForUser() {
        doNothing().when(ticketRepository).deleteByUserId("123");

        ticketService.deleteTicketsForUser("123");

        verify(ticketRepository, times(1)).deleteByUserId("123");
    }

    @Test
    void testCreateTicketThrowsExceptionForInvalidEmail() {
        CreateTicketRequestDTO requestDTO = new CreateTicketRequestDTO();
        requestDTO.setUserId("123");

        User user = new User();
        user.setId("123");
        user.setEmail("");
        when(userService.getUserById("123")).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> ticketService.createTicket(requestDTO));
    }
}
