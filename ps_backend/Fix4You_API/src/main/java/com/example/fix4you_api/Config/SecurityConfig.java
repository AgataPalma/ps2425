package com.example.fix4you_api.Config;

import com.example.fix4you_api.Auth.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.RequestContextFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RequestContextFilter requestContextFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login").permitAll()    // all users are allowed
                        .requestMatchers("/users").hasAnyRole("ADMIN","PROFESSIONAL")    // if only the role USER is allowed (hasRole("CLIENT"))
                        .requestMatchers("/users/email-confirmation/**").permitAll()
                        .requestMatchers("/professionals").permitAll()
                        .requestMatchers("/users/resetPasswordToken/**").permitAll()
                        .requestMatchers("/users/resetPassword").hasAnyRole("ADMIN","PROFESSIONAL","CLIENT")
                        .requestMatchers("/categoryDescriptions").permitAll()
                        .requestMatchers("/categoryDescriptions/*").permitAll()
                        .requestMatchers("/categoryDescriptions/user/*").permitAll()
                        .requestMatchers("/portfolioItems").permitAll()
                        .requestMatchers("/portfolioItems/*").permitAll()
                        .requestMatchers("/portfolioItems/user/*").permitAll()
                        .requestMatchers("/professionalFees").permitAll()
                        .requestMatchers("/professionalFees/*").permitAll()
                        .requestMatchers("/professionalFees/user/*").permitAll()
                        .anyRequest().authenticated())
                        .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add the JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
