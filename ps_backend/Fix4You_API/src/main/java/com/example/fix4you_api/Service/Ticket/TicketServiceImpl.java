package com.example.fix4you_api.Service.Ticket;

import com.example.fix4you_api.Data.Enums.TicketStatusEnum;
import com.example.fix4you_api.Data.Models.Dtos.SimpleUserDTO;
import com.example.fix4you_api.Data.Models.Ticket;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.TicketRepository;
import com.example.fix4you_api.Service.Email.EmailSenderService;
import com.example.fix4you_api.Service.User.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    @Value("${spring.mail.username}")
    private String appEmail;

    private final TicketRepository ticketRepository;

    private final UserService userService;
    private final EmailSenderService emailSenderService;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket createTicket(Ticket ticket, String description) throws MessagingException {
        ticket.setStatus(TicketStatusEnum.NEW);
        ticket.setTicketStartDate(LocalDateTime.now());

        User user = userService.getUserById(ticket.getUser().getId());

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("O usuário associado ao ticket deve ter um e-mail válido.");
        }

        sendEmailConfirmationToUser(ticket.getTitle(), user.getEmail(), description);
        sendEmailConfirmationToApp(ticket.getTitle(), user.getEmail(), description);

        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public Ticket updateTicket(String id, Ticket ticket) {
        Ticket existingCTicket = findOrThrow(id);
        BeanUtils.copyProperties(ticket, existingCTicket, "id");
        return ticketRepository.save(existingCTicket);
    }

    @Override
    @Transactional
    public Ticket partialUpdateTicket(String id, Map<String, Object> updates) {
        Ticket existingTicket = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "admin" -> existingTicket.setAdmin((SimpleUserDTO) value);
                case "title" ->existingTicket.setTitle((String) value);
                case "status" -> {
                    try {
                        existingTicket.setStatus(TicketStatusEnum.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Valor inválido para o estado: " + value);
                    }
                }
                case "ticketStartDate" -> {
                    if (value instanceof LocalDateTime) {
                        existingTicket.setTicketStartDate((LocalDateTime) value);
                    } else if (value instanceof String) {
                        existingTicket.setTicketStartDate(LocalDateTime.parse((CharSequence) value));
                    }
                }
                case "ticketCloseDate" -> {
                    if (value instanceof LocalDateTime) {
                        existingTicket.setTicketCloseDate((LocalDateTime) value);
                    } else if (value instanceof String) {
                        existingTicket.setTicketCloseDate(LocalDateTime.parse((CharSequence) value));
                    }
                }
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

        return ticketRepository.save(existingTicket);
    }

    @Override
    @Transactional
    public void deleteTicket(String id) {
        ticketRepository.deleteById(id);
    }

    private Ticket findOrThrow(String id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Ticket %s não encontrado!", id)));
    }

    @Override
    @Transactional
    public void deleteTicketsForUser(String userId) {
        ticketRepository.deleteByUserId(userId);
    }

    private void sendEmailConfirmationToUser(String title, String userEmail, String description) throws MessagingException {
        String body = """
        <html>
            <body>
                <p>Olá,</p>
                <br>
                <p>Estamos a enviar este e-mail para confirmar a submissão do seu ticket '<strong>%s</strong>':</p>
                <p><strong>%s</strong></p>
                <br>
                <p>Muito obrigado pela sua colaboração,</p>
                <p>Fix4You</p>
            </body>
        </html>
        """.formatted(title, description);

        emailSenderService.sendEmail(userEmail, "Fix4You - Confirmação de Ticket", body);
    }

    private void sendEmailConfirmationToApp(String title, String userEmail, String description) throws MessagingException {
        String body = """
        <html>
            <body>
                <p>O utilizador %s submeteu um novo ticket '<strong>%s</strong>':</p>
                <p><strong>%s</strong></p>
            </body>
        </html>
        """.formatted(userEmail, title, description);

        emailSenderService.sendEmail(appEmail, "Fix4You - Novo Ticket", body);
    }

}