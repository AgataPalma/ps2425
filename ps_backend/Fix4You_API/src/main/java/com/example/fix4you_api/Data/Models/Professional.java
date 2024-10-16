package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Document("Professionals")
public class Professional extends User {

    @Field
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Field
    @NotBlank(message = "NIF cannot be blank")
    @Size(min = 9, max = 9, message = "NIF must be 9 characters long")
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
