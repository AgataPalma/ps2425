package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@AllArgsConstructor
@NoArgsConstructor
@Document("Users")
public class User {

    @Id
    private String id;

    @Field
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Field
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @Field
    @PastOrPresent(message = "Creation date must be in the past or present")
    @NotNull(message = "Date of creation cannot be null")
    private LocalDateTime dateCreation;

    @Field
    @NotNull(message = "User type cannot be null")
    private EnumUserType userType;
}