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
    @NotNull(message = "Token cannot be null")
    private String userId;

    @Field
    @NotNull(message = "expiryDateTime cannot be null")
    private LocalDateTime expiryDateTime;

    public PasswordResetToken(String token, LocalDateTime expiryDateTime, String userId) {
        this.token = token;
        this.expiryDateTime = expiryDateTime;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public @NotBlank(message = "Token cannot be blank") String getToken() {
        return token;
    }

    public @NotNull(message = "Token cannot be null") String getUserId() {
        return userId;
    }

    public @NotNull(message = "expiryDateTime cannot be null") LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(@NotNull(message = "Token cannot be null") String userId) {
        this.userId = userId;
    }

    public void setExpiryDateTime(@NotNull(message = "expiryDateTime cannot be null") LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }
}