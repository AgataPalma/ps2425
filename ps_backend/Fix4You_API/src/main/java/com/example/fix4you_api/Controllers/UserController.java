package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Auth.JwtUtil;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public String createToken() throws Exception {
        try {

        } catch (Exception e) {
            throw new Exception("Invalid credentials", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername("rodri");
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return jwt;
    }

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
