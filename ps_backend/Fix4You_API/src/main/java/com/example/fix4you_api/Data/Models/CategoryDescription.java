package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.EnumCategories;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("CategoryDescriptions")
public class CategoryDescription {

    @Id
    private String id;

    @Field
    @NotNull(message = "Professional ID cannot be null")
    private String idProfessional;

    @Field
    @NotNull(message = "Category cannot be null")
    private EnumCategories category;

    @Field
    @NotNull(message = "Charges travels cannot be null")
    private boolean chargesTravels;

    @Field
    @NotNull(message = "Provides invoices cannot be null")
    private boolean providesInvoices;

    @Field
    @NotNull(message = "Medium price per service cannot be null")
    @DecimalMin(value = "0.0", message = "Medium price must be a positive value")
    @Digits(integer = 5, fraction = 2, message = "Medium price must be a numeric value with up to 5 digits and 2 decimal places")
    private float mediumPricePerService;
}