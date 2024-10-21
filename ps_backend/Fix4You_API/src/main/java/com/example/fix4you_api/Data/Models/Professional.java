package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import jakarta.validation.constraints.*;
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
    @Pattern(regexp = "\\d{9}", message = "NIF must contain exactly 9 digits")
    private String nif;

    @Field
    @NotBlank(message = "Location cannot be blank")
    private String location;

    @Field
    @NotNull(message = "Locations range cannot be null")
    private int locationsRange;

    @Field
    @NotNull(message = "Accepted payments cannot be null")
    private List<PaymentTypesEnum> acceptedPayments;

    @Field
    private boolean isCompany;
}