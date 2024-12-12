package com.example.fix4you_api.Service.User;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.TicketStatusEnum;
import com.example.fix4you_api.Data.Models.PasswordResetToken;
import com.example.fix4you_api.Data.Models.Ticket;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.PasswordResetTokenRepository;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import com.example.fix4you_api.Service.Email.EmailSenderService;
import com.example.fix4you_api.Service.Login.LoginRequest;
import com.example.fix4you_api.Service.Ticket.TicketService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final EmailSenderService emailSenderService;
    private final TicketService ticketService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllAdmins() {
        return userRepository.findByUserType(EnumUserType.ADMIN);
    }

    @Override
    public User getAdminById(String id) {
        return findOrThrowAdmin(id);
    }

    public User getUser(String id) {
        return find(id);
    }

    @Override
    public User getUserById(String id) {
        return findOrThrow(id);
    }

    @Override
    public User createUser(User user) {
        user.setUserType(EnumUserType.ADMIN);
        user.setDateCreation(LocalDateTime.now());
        user.setIsEmailConfirmed(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(String id, User user) {
        User existingUser = findOrThrow(user.getId());
        BeanUtils.copyProperties(user, existingUser, "id");
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User partialUpdateUser(String id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilizador não encontrado!"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "email" -> user.setEmail((String) value);
                case "password" -> user.setPassword((String) value);
                case "userType" -> {
                    try {
                        user.setUserType(EnumUserType.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Valor inválido para o estado: " + value);
                    }
                }
                case "IsEmailConfirmed" -> user.setIsEmailConfirmed((Boolean) value);
                //case "IsDeleted" -> user.setIsDeleted((Boolean) value);
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!\n");
            }
        });

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        User existingUser = findOrThrow(id);
        //existingUser.setIsDeleted(true);
        //return userRepository.save(existingUser);

        List<User> admins = getAllAdmins();

        if(admins.size() == 1 && Objects.equals(admins.get(0).getId(), id)) {
            throw new IllegalStateException("Não pode apagar o admin, pois é o único.");
        } else if(existingUser.getUserType() == EnumUserType.ADMIN){
            List<Ticket> tickets = ticketService.getTicketsByAdminId(id);

            for(Ticket ticket: tickets) {
                if(ticket.getStatus() == TicketStatusEnum.IN_REVIEW) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("status", TicketStatusEnum.NEW);
                    updates.put("admin", null);
                    updates.put("adminAssignmentDate", null);


                    ticketService.partialUpdateTicket(ticket.getId(), updates);
                }
            }
        }

        userRepository.deleteById(id);
    }

    private User find(String id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            return user.get();
        }
        return null;
    }

    private User findOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Utilizador %s não encontrado!", id)));
    }

    private User findOrThrowAdmin(String id) {
        return userRepository.findById(id)
                .filter(user -> user.getUserType() == EnumUserType.ADMIN)
                .orElseThrow(() -> new NoSuchElementException(String.format("Utilizador %s não encontrado! Ou o utilizador não é um admin", id)));
    }

    @Override
    public User loginUser(LoginRequest request) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if(user.getEmail().equals(request.getEmail())) {
                // Success
                if(user.getPassword().equals(request.getPassword())) {
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public void sendEmailTopUsers(User user) {
        try{
            String msg = "Olá.<br>" +
                    "Obrigado por nos ajudar a crescer." +
                    "Valorizamos a sua colaboração como colaborador de topo!";

            String body = "<p>" + msg + "</p>";
            body += "<p>Juntos somos mais fortes.</p>";
            body += "<p>Melhores Cumprimentos.</p>";
            body += "<p>A equipa Fix4You</p>";

            emailSenderService.sendEmail(user.getEmail(),
                    "Obrigado pelo seu apoio",
                    body);
        } catch (Exception e) {
            System.err.println("[ERROR] - " + e.getMessage());
        }
    }

    @Override
    public boolean sendEmailWithVerificationToken(User user) throws MessagingException {
        String resetLink = generateResetToken(user);
        String body = "Olá \n\n" + "Clique neste link para redefinir a sua palavra-passe: " + resetLink + " \n\n";

        emailSenderService.sendEmail(user.getEmail(),
                "Verificar OTP Fix4You",
                body);

        return true;
    }

    @Override
    public void sendValidationEmailUserRegistration(String email) {
        try {
            String endpointURL = "http://localhost:8080/users/email-confirmation/" + email;
            String body = "Olá \n\n" + "Clique neste link para confirmar o seu endereço de email e completar o seu registo na Fix4You: " + endpointURL + " \n\n";

            emailSenderService.sendEmail(email,
                    "Confirmação de email Fix4You",
                    body);
        } catch (Exception e) {
            System.err.println("[ERROR] - " + e.getMessage());
        }

    }

    @Override
    public boolean emailExists(String email) {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if(user.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    private String generateResetToken(User user) {
        UUID uuid = UUID.randomUUID();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = currentDateTime.plusMinutes(30);
        boolean flagChanged = false;
        PasswordResetToken newToken = null;

        // check if there is already a userId created in passwordResetTokenRepository
        List<PasswordResetToken> tokens = passwordResetTokenRepository.findAll();
        for (PasswordResetToken token : tokens) {
            if(token.getUserId().equals(user.getId())) {
                token.setExpiryDateTime(expiryDateTime);
                token.setToken(uuid.toString());
                passwordResetTokenRepository.save(token);
                newToken = token;
                flagChanged = true;
            }
        }

        if(!flagChanged) {
            newToken = new PasswordResetToken(uuid.toString(),expiryDateTime, user.getId());
            passwordResetTokenRepository.save(newToken);
        }

        if(newToken != null) {
            String endpointUrl = "http://localhost:8080/users/resetPasswordToken";
            return endpointUrl + "/" + newToken.getToken();
        }

        return "";
    }
}