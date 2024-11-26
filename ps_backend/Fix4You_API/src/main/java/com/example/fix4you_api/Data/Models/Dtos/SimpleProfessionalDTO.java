package com.example.fix4you_api.Data.Models.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleProfessionalDTO {
    @Field
    @NotBlank(message = "ID cannot be blank")
    private String id;

    @Field
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Field
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Name must be less than 500 characters")
    private String name;

    @Field
    @NotBlank(message = "NIF cannot be blank")
    @Pattern(regexp = "^[0-46-9]\\d{8}$", message = "NIF must contain exactly 9 digits and cannot start with 5")
    private String nif;
}