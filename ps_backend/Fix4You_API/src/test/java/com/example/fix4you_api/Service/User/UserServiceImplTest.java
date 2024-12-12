package com.example.fix4you_api.Service.User;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.PasswordResetTokenRepository;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import com.example.fix4you_api.Service.Email.EmailSenderService;
import com.example.fix4you_api.Service.Login.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllAdmins() {
        List<User> admins = Arrays.asList(new User(), new User());
        when(userRepository.findByUserType(EnumUserType.ADMIN)).thenReturn(admins);

        List<User> result = userService.getAllAdmins();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findByUserType(EnumUserType.ADMIN);
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId("123");
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        User result = userService.getUserById("123");

        assertNotNull(result);
        assertEquals("123", result.getId());
        verify(userRepository, times(1)).findById("123");
    }

    @Test
    void testCreateUser() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User();
        existingUser.setId("123");
        when(userRepository.findById("123")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = new User();
        updatedUser.setId("123");
        updatedUser.setEmail("newemail@example.com");

        User result = userService.updateUser("123", updatedUser);

        assertNotNull(result);
        assertEquals("newemail@example.com", result.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById("123");

        userService.deleteUser("123");

        verify(userRepository, times(1)).deleteById("123");
    }

    @Test
    void testLoginUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        LoginRequest request = new LoginRequest("test@example.com", "password");

        User result = userService.loginUser(request);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testEmailExists() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        boolean exists = userService.emailExists("test@example.com");

        assertTrue(exists);
    }
}
