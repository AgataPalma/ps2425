package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Email(message = "Email tem de ser válido")
    @NotBlank(message = "Email não pode estar em branco")
    private String email;

    @Field
    @NotBlank(message = "Password não pode estar em branco")
    private String password;

    @Field
    private LocalDateTime dateCreation;

    @Field
    @NotNull(message = "User type não pode ser nulo")
    private EnumUserType userType;

    @Field
    @NotNull(message = "confirmação de email não pode ser nulo")
    private boolean IsEmailConfirmed;

    //@Field
    //private boolean IsDeleted;
}