package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Auth.JwtResponse;
import com.example.fix4you_api.Auth.JwtUtil;
import com.example.fix4you_api.Data.Models.PasswordResetToken;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.PasswordResetTokenRepository;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import com.example.fix4you_api.Service.Login.DTOs.ResponseLogin;
import com.example.fix4you_api.Service.Login.LoginRequest;
import com.example.fix4you_api.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> users = userService.getAllAdmins();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody LoginRequest request) {
        // retrieve data from dataBase
        User userLogin = userService.loginUser(request);
        if(userLogin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong email or password!");
        }

        // check if email is confirmed
        if(!userLogin.isIsEmailConfirmed()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You need to confirm your email!");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(),userLogin.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(userLogin.getEmail());
        final String jwt = jwtUtil.generateToken(userLogin.getEmail());
        ResponseLogin response = new ResponseLogin(jwt, userLogin.getId(), userLogin.getUserType());
        //JwtResponse jwtResponse = new JwtResponse(jwt);

        return ResponseEntity.ok(response);
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
    public RedirectView resetPasswordForm(@PathVariable String token) {
        List<PasswordResetToken> listToken = passwordResetTokenRepository.findAll();
        PasswordResetToken passwordResetToken = null;
        for (PasswordResetToken currentToken : listToken) {
            if (currentToken.getToken().equals(token)) {
                passwordResetToken = currentToken;
                break;
            }
        }

        // Se o token é válido e ainda não expirou
        if (passwordResetToken != null && passwordResetToken.getExpiryDateTime().isAfter(LocalDateTime.now())) {
            String redirectUrl = "http://localhost:3000/PasswordReset?token="+token;  // URL frontend para resetar senha
            return new RedirectView(redirectUrl);
        }

        return new RedirectView("http://localhost:3000/forgot-password?error=invalidToken");
    }


    @PostMapping("/resetPassword")
    public ResponseEntity<String> passwordResetProcess(@RequestBody Map<String, String> requestBody) {
        // Extrai os parâmetros do corpo da solicitação
        String email = requestBody.get("email");
        String token = requestBody.get("token");
        String password = requestBody.get("password");

        // Busca o token no repositório
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        // Verifica se o token ainda é válido
        if (resetToken.getExpiryDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired");
        }

        // Usa o userId do token para encontrar o usuário no repositório
        User user = userRepository.findById(resetToken.getUserId()).orElse(null);
        if (user == null || !user.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email or token");
        }

        // Redefine a senha do usuário e salva
        user.setPassword(password); // Certifique-se de aplicar hash na senha antes de salvar
        userRepository.save(user);

        // Remove o token após o uso
        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok("Password reset successfully");
    }


    @GetMapping("/email-confirmation/{email}")
    public ResponseEntity<?> emailConfirmation(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            return ResponseEntity.ok("User not found");
        }

        user.setIsEmailConfirmed(true);
        userRepository.save(user);
        return ResponseEntity.ok("Email confirmed");
    }
}
