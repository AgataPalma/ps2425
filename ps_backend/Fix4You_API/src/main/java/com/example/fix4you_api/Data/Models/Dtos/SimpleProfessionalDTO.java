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
    @NotBlank(message = "ID não pode estar em branco")
    private String id;

    @Field
    @Email(message = "Email tem de ser válido")
    @NotBlank(message = "Email não pode estar em branco")
    private String email;

    @Field
    @NotBlank(message = "O nome não pode estar em branco")
    @Size(max = 50, message = "O nome must be less than 500 characters")
    private String name;

    @Field
    @NotBlank(message = "NIF não pode estar em branco")
    @Pattern(regexp = "^[0-46-9]\\d{8}$", message = "O NIF deve conter exatamente 9 dígitos e não pode começar por 5")
    private String nif;
}