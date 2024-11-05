package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.EnumCategories;
import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
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
@Document("Services")
public class Service {

    @Id
    private String id;

    @Field
    @NotNull(message = "Client ID cannot be null")
    private String clientId;

    @Field
    @NotNull(message = "Professional ID cannot be null")
    private String professionalId;

    @Field
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", message = "Price must be a positive value")
    @Digits(integer = 5, fraction = 2, message = "Price must be a numeric value with up to 5 digits and 2 decimal places")
    private float price;

    @Field
    @NotBlank(message = "Address cannot be blank")
    private String address;

    @Field
    @NotBlank(message = "Postal Code cannot be blank")
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,10}$", message = "Postal Code must be alphanumeric and between 3 and 10 characters")
    private String postalCode;

    @Field
    @NotNull(message = "Category cannot be null")
    private EnumCategories category;

    @Field
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Field
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Field
    @NotNull(message = "State cannot be null")
    private ServiceStateEnum state;

    @Field
    @NotNull(message = "isUrgent cannot be null")
    private boolean isUrgent;
}
