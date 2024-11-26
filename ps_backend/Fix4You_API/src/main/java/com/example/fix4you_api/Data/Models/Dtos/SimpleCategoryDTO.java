package com.example.fix4you_api.Data.Models.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCategoryDTO {
    @Field
    @NotBlank(message = "ID cannot be blank")
    private String id;

    @Field
    @NotBlank(message = "Name cannot be blank")
    private String name;
}