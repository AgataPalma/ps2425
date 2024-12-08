package com.example.fix4you_api.Data.Models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("GoogleTokenUser")
public class GoogleTokenUser {
    @Id
    private String id;

    @Field
    private String token;

    @Field
    private String refreshToken;

    @Field
    @NotNull(message = "UserId não pode ser nulo")
    private String userId;

    @Field
    @NotNull(message = "email não pode ser nulo")
    private String email;
}