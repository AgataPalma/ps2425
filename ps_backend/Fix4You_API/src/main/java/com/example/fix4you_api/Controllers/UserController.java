package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        try {
            List<User> users = userRepository.findAll();

            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users found.");
            }

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
