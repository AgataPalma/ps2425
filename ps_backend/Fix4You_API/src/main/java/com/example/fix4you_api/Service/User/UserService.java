package com.example.fix4you_api.Service.User;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Service.Login.LoginRequest;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> getAllUsers();

    User getUser(String id);

    User getUserById(String id);

    User getAdminById(String id);

    User createUser(User user);

    User updateUser(String id, User user);

    User partialUpdateUser(String id, Map<String, Object> updates);

    void deleteUser(String id);

    User loginUser(LoginRequest request);

    List<User> getAllAdmins();

    boolean sendEmailWithVerificationToken(User user) throws MessagingException;

    void sendValidationEmailUserRegistration(String email);

    void sendEmailTopUsers(User user);

    boolean emailExists(String email);
}
