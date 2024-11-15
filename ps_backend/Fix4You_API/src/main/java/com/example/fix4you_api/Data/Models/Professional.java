package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.LanguageEnum;
import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class Professional extends Client {

    @Field
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @Field
    @NotBlank(message = "NIF cannot be blank")
    @Pattern(regexp = "^[0-46-9]\\d{8}$", message = "NIF must contain exactly 9 digits and cannot start with 5")
    private String nif;

    @NotNull(message = "Languages cannot be null")
    private List<LanguageEnum> languages;

    @Field
    @NotNull(message = "Locations range cannot be null")
    private int locationsRange;

    @Field
    @NotNull(message = "Accepted payments cannot be null")
    private List<PaymentTypesEnum> acceptedPayments;

    @Field
    private int strikes;

    @Field
    private boolean isSupended;
}