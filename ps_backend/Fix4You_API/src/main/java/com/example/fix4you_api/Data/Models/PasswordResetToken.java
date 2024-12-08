package com.example.fix4you_api.Data.Models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document("PasswordResetToken")
public class PasswordResetToken {

    @Id
    private String id;

    @Field
    private String token;

    @Field
    @NotNull(message = "Token não pode ser nulo")
    private String userId;

    @Field
    @NotNull(message = "expiryDateTime não pode ser nulo")
    private LocalDateTime expiryDateTime;

    public PasswordResetToken(String token, LocalDateTime expiryDateTime, String userId) {
        this.token = token;
        this.expiryDateTime = expiryDateTime;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public @NotBlank(message = "Token não pode estar em branco") String getToken() {
        return token;
    }

    public @NotNull(message = "Token não pode ser nulo") String getUserId() {
        return userId;
    }

    public @NotNull(message = "expiryDateTime não pode ser nulo") LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(@NotNull(message = "Token não pode ser nulo") String userId) {
        this.userId = userId;
    }

    public void setExpiryDateTime(@NotNull(message = "expiryDateTime não pode ser nulo") LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }
}