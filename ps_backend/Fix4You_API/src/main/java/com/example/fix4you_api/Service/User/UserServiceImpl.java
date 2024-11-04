package com.example.fix4you_api.Service.User;

import com.example.fix4you_api.Data.Models.PasswordResetToken;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.PasswordResetTokenRepository;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import com.example.fix4you_api.Service.Email.EmailSenderService;
import com.example.fix4you_api.Service.Login.LoginRequest;
import com.example.fix4you_api.Utils.Encrypt;
import com.example.fix4you_api.Utils.RandomDigitStringGenerator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.apache.tomcat.util.descriptor.tagplugin.TagPluginParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public User getUserById(String id) {
        return findOrThrow(id);
    }

    @Override
    public User createUser(User user) {
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
    public void deleteUser(String id) {
        User user = findOrThrow(id);
        userRepository.delete(user);
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
        String body = "Hello \n\n" + "Please click on this link to Reset your Password :" + resetLink + ". \n\n";

        emailSenderService.sendSimpleEmail(user.getEmail(),
                "Verify OTP Fix4You",
                body);

        return true;
    }

    @Override
    public void sendValidationEmailUserRegistration(String email) {
        try {
            String endpointURL = "http://localhost:8080/users/email-confirmation/" + email;
            String body = "Hello \n\n" + "Please Click on this link to confirm your email address and complete your registration at Fix4You:" + endpointURL + ". \n\n";

            emailSenderService.sendSimpleEmail(email,
                    "Email confirmation Fix4You",
                    body);
        } catch (Exception e) {
            System.err.println("[ERROR] - " + e.getMessage());
        }

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
