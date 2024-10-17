package com.example.fix4you_api.Auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public MyUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("user".equals(username)) {
            return org.springframework.security.core.userdetails.User
                    .withUsername("user")
                    .password(passwordEncoder.encode("password")) // Use encoded password
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}