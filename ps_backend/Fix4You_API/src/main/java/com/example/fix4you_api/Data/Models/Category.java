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
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Description must be less than 500 characters")
    private String name;

    @Field
    @NotNull(message = "MinValue cannot be null")
    @DecimalMin(value = "0.0", message = "MinValue must be a positive value")
    @Digits(integer = 5, fraction = 2, message = "MinValue must be a numeric value with up to 3 digits and 2 decimal places")
    private float minValue;

    @Field
    @NotNull(message = "MaxValue cannot be null")
    @DecimalMin(value = "0.0", message = "MaxValue must be a positive value")
    @Digits(integer = 5, fraction = 2, message = "MaxValue must be a numeric value with up to 3 digits and 2 decimal places")
    private float maxValue;
}