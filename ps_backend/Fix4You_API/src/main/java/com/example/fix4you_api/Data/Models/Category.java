package com.example.fix4you_api.Data.Models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("Categories")
public class Category {
    @Id
    private String id;

    @Field
    @NotBlank(message = "O nome não pode ser nulo")
    @Size(max = 50, message = "O Nome tem de ter menos de 50 caracteres")
    private String name;

    @Field
    @NotNull(message = "MinValue não pode ser nulo")
    @DecimalMin(value = "0.0", message = "MinValue tem de ter um valor positivo")
    @Digits(integer = 5, fraction = 2, message = "MinValue deve ser um valor numérico com um máximo de 3 dígitos e 2 casas decimais")
    private float minValue;

    @Field
    @NotNull(message = "MaxValue não pode ser nulo")
    @DecimalMin(value = "0.0", message = "MaxValue tem de ser um valor positivo")
    @Digits(integer = 5, fraction = 2, message = "MaxValue deve ser um valor numérico com um máximo de 3 dígitos e 2 casas decimais")
    private float maxValue;

    @Field
    private float medianValue;

    @Field
    private Integer completedServices;
}