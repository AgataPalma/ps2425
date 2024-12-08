package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Dtos.SimpleCategoryDTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("Services")
public class Service {
    @Id
    private String id;

    @Field
    @NotNull(message = "O ID do cliente não pode ser nulo")
    private String clientId;

    @Field
    private String professionalId;

    @Field
    @NotNull(message = "O preço não pode ser nulo")
    @DecimalMin(value = "0.0", message = "O preço deve ser um valor positivo")
    @Digits(integer = 5, fraction = 2, message = "O preço deve ser um valor numérico com um máximo de 5 dígitos e 2 casas decimais")
    private float price;

    @Field
    @NotBlank(message = "O endereço não pode estar em branco")
    private String address;

    @Field
    @NotBlank(message = "O código postal não pode estar em branco")
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,10}$", message = "O código postal deve ser alfanumérico e ter entre 3 e 10 caracteres")
    private String postalCode;

    @Field
    @NotNull(message = "A categoria não pode ser nula")
    private SimpleCategoryDTO category;

    @Field
    @NotBlank(message = "A descrição não pode estar em branco")
    private String description;

    @Field
    @NotBlank(message = "O título não pode estar em branco")
    private String title;

    @Field
    @NotNull(message = "O Estado não pode ser nulo")
    private ServiceStateEnum state;

    @Field
    @NotNull(message = "isUrgent não pode ser nulo")
    private boolean isUrgent;

    @Field
    private LocalDateTime dateCreation;

    @Field
    private LocalDateTime agreementDate;

    @Field
    @NotNull(message = "As línguas não podem ser nulas")
    private List<Language> languages;

    @Field
    @NotBlank(message = "A localização não pode estar em branco")
    private String location;
}