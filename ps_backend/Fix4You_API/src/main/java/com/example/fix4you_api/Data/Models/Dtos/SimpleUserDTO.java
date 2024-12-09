package com.example.fix4you_api.Data.Models.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserDTO {
    @Field
    @NotBlank(message = "ID não pode estar em branco")
    private String id;

    @Field
    @Email(message = "Email tem de ser válido")
    @NotBlank(message = "Email não pode estar em branco")
    private String email;
}