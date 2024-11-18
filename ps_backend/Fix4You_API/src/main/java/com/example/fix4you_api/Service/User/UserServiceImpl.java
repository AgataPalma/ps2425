package com.example.fix4you_api.Service.User;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.PasswordResetToken;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.PasswordResetTokenRepository;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import com.example.fix4you_api.Service.Email.EmailSenderService;
import com.example.fix4you_api.Service.Login.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final EmailSenderService emailSenderService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllAdmins() {
        return userRepository.findByUserType(EnumUserType.ADMIN);
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
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "password" -> user.setPassword((String) value);
                case "email" -> user.setEmail((String) value);
                case "userType" -> user.setUserType((EnumUserType) value);
                case "IsEmailConfirmed" -> user.setIsEmailConfirmed((Boolean) value);
                //case "IsDeleted" -> user.setIsDeleted((Boolean) value);
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        //User existingUser = findOrThrow(id);
        //existingUser.setIsDeleted(true);
        //return userRepository.save(existingUser);

        userRepository.deleteById(id);
    }

    private User findOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %s not found", id)));
    }

    private User findByEmailOrThrow(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new NoSuchElementException(String.format("User %s not found", email));
        }
        return user;
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
    public boolean sendEmailWithVerificationToken(User user) {
        String resetLink = generateResetToken(user);
        String body = "Hello \n\n" + "Please click on this link to Reset your Password: " + resetLink + " \n\n";

        emailSenderService.sendEmail(user.getEmail(),
                "Verify OTP Fix4You",
                body);

        return true;
    }

    @Override
    public void sendValidationEmailUserRegistration(String email) {
        try {
            String endpointURL = "http://localhost:8080/users/email-confirmation/" + email;
            String body = "Hello \n\n" + "Please Click on this link to confirm your email address and complete your registration at Fix4You: " + endpointURL + " \n\n";

            emailSenderService.sendEmail(email,
                    "Email confirmation Fix4You",
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
