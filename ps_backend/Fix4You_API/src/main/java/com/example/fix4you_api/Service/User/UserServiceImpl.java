package com.example.fix4you_api.Service.User;

import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import com.example.fix4you_api.Service.Login.LoginRequest;
import com.example.fix4you_api.Utils.Encrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
}