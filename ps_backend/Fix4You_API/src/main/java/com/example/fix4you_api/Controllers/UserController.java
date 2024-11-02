package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Auth.JwtResponse;
import com.example.fix4you_api.Auth.JwtUtil;
import com.example.fix4you_api.Data.Models.PasswordResetToken;
import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.PasswordResetTokenRepository;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import com.example.fix4you_api.Service.Email.EmailSenderService;
import com.example.fix4you_api.Service.Login.LoginRequest;
import com.example.fix4you_api.Service.User.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("users")
public class UserController {

    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private PasswordResetTokenRepository passwordResetTokenRepository;

    public UserController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService, PasswordResetTokenRepository passwordResetToken) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordResetTokenRepository = passwordResetToken;
    }

    @GetMapping
    public ResponseEntity<?> getUsers(@PathVariable String type) {
        List<User> users = null;
        if(Objects.equals(type, "PROFESSIONAL")) {
            users = this.userRepository.findByUserType(EnumUserType.PROFESSIONAL);
        } else if(Objects.equals(type, "CLIENT")) {
            users = this.userRepository.findByUserType(EnumUserType.CLIENT);
        } else if(Objects.equals(type, "ADMIN")) {
            users = this.userRepository.findByUserType(EnumUserType.ADMIN);
        } else {
            users = userService.getAllUsers();
        }

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found.");
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(String.format("User %s deleted", id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody LoginRequest request) {
        // retrieve data from dataBase
        User userLogin = userService.loginUser(request);
        if(userLogin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong email or password!");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(),userLogin.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(userLogin.getEmail());
        final String jwt = jwtUtil.generateToken(userLogin.getEmail());
        JwtResponse jwtResponse = new JwtResponse(jwt);

        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/send-email-verification/{email}")
    public ResponseEntity<?> sendEmail(@PathVariable String email) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.ok("User not found");
        }

        userService.sendEmailWithVerificationToken(user);

        return ResponseEntity.ok("Email sent successfully");
    }

    @GetMapping("/resetPasswordToken/{token}")
    public String resetPasswordForm(@PathVariable String token) {
        List<PasswordResetToken> listToken = passwordResetTokenRepository.findAll();
        PasswordResetToken passwordResetToken = null;
        for (PasswordResetToken currentToken : listToken) {
            if(currentToken.getToken().equals(token)) {
                passwordResetToken = currentToken;
            }
        }
        // token valid
        if (passwordResetToken != null && passwordResetToken.getExpiryDateTime().isAfter(LocalDateTime.now())) {
            // redirect to the page to forget Password
            return "resetPassword";
        }

        return "redirect:/forgotPassword?error";
    }

    @PostMapping("/resetPassword")
    public String passwordResetProcess(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if(user != null) {
            user.setPassword(request.getPassword());
            userRepository.save(user);
        }
        return "redirect:/login";
    }
}
