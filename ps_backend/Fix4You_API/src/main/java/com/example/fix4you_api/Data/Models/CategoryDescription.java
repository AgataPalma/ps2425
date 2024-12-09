package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Models.Dtos.SimpleCategoryDTO;
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
    @NotNull(message = "Professional ID não pode ser nulo")
    private String professionalId;

    @Field
    private SimpleCategoryDTO category;

    @Field
    @NotNull(message = "Cobra por deslocação não pode ser nulo")
    private boolean chargesTravels;

    //@Field
    //@NotNull(message = "Provides invoices cannot be null")
    //private boolean providesInvoices;

    @Field
    @NotNull(message = "Preço médio por serviço não pode ser nulo")
    @DecimalMin(value = "0.0", message = "Preço médio tem de ser um valor positivo")
    @Digits(integer = 5, fraction = 2, message = "O preço médio deve ser um valor numérico com um máximo de 5 dígitos e 2 casas decimais")
    private float mediumPricePerService;

}