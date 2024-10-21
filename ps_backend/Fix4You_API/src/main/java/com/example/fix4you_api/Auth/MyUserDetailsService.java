package com.example.fix4you_api.Auth;

import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    public MyUserDetailsService(PasswordEncoder passwordEncoder, UserRepository userRepository) throws Exception {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) { throw new UsernameNotFoundException(email); }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword())) // Use encoded password
                .roles(user.getUserType().toString())
                .build();
    }
}