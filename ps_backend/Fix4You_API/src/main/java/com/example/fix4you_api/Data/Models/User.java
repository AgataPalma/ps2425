package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.LanguageEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

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
    @JsonIgnore
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @Field
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Field
    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 9, max = 9, message = "Phone number must be 9 digits")
    @Pattern(regexp = "^\\+?[0-9\\-\\s()]*$", message = "Phone number must be valid and may contain only numbers, spaces, '-', '(', and ')'")
    private String phoneNumber;

    @Field
    private LocalDateTime dateCreation;

    @Field
    @NotNull(message = "Languages cannot be null")
    private List<LanguageEnum> languages;

    @Field
    @NotNull(message = "User type cannot be null")
    private EnumUserType userType;

    @Field
    @NotNull(message = "Profile image cannot be null")
    @Size(max = 1048576, message = "Profile image must be less than 1 MB") // Exemplo de limite de tamanho de 1MB
    private byte[] profileImage;

    @Field
    private boolean ageValidation;

}
