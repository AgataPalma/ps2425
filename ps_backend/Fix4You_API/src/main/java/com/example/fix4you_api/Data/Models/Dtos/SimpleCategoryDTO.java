package com.example.fix4you_api.Data.Models.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCategoryDTO {
    @Field
    @NotBlank(message = "ID não pode estar em branco")
    private String id;

    @Field
    @NotBlank(message = "O nome não pode estar em branco")
    @Size(max = 50, message = "O nome deve ter menos de 50 caracteres")
    private String name;
}