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
                        .requestMatchers("/users").hasAnyRole("ADMIN","PROFESSIONAL")   // if only the role USER is allowed (hasRole("CLIENT"))
                        .requestMatchers("/users/admins").hasAnyRole("ADMIN","PROFESSIONAL")
                        .requestMatchers("/users/email-confirmation/**").permitAll()
                        .requestMatchers("/users/send-email-verification/**").permitAll()
                        .requestMatchers("/professionals").permitAll()
                        .requestMatchers("/professionals/**").permitAll()
                        .requestMatchers("/clients").permitAll()
                        .requestMatchers("/clients/*").permitAll()
                        .requestMatchers("/users/resetPasswordToken/**").permitAll()
                        .requestMatchers("/users/resetPassword").hasAnyRole("ADMIN","PROFESSIONAL","CLIENT")
                        .requestMatchers("/categories").permitAll()
                        .requestMatchers("/categoryDescriptions").permitAll()
                        .requestMatchers("/categoryDescriptions/*").permitAll()
                        .requestMatchers("/categoryDescriptions/user/*").permitAll()
                        .requestMatchers("/portfolioItems").permitAll()
                        .requestMatchers("/portfolioItems/*").permitAll()
                        .requestMatchers("/portfolioItems/user/*").permitAll()
                        .requestMatchers("/professionalFees").permitAll()
                        .requestMatchers("/professionalFees/*").permitAll()
                        .requestMatchers("/professionalFees/user/*").permitAll()
                        .requestMatchers("/services").permitAll()
                        .requestMatchers("/services/*").permitAll()
                        .requestMatchers("/services/professional/*").permitAll()
                        .requestMatchers("/services/client/*").permitAll()
                        .requestMatchers("/scheduleAppointments").permitAll()
                        .requestMatchers("/scheduleAppointments/*").permitAll()
                        .requestMatchers("/scheduleAppointments/approve/*").hasAnyRole("ADMIN","PROFESSIONAL")
                        .requestMatchers("/scheduleAppointments/disapprove/*").hasAnyRole("ADMIN","PROFESSIONAL")
                        .requestMatchers("/scheduleAppointments/professional/*").permitAll()
                        .requestMatchers("/scheduleAppointments/client/*").permitAll()
                        .requestMatchers("/professional-category-views").permitAll()
                        .requestMatchers("/professional-category-views/*").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                        //.anyRequest().permitAll())
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
