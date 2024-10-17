package com.example.fix4you_api.Service.User;

import com.example.fix4you_api.Data.Models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(String id);

    User createUser(User user);

    User updateUser(String id, User user);

    void deleteUser(String id);
}
