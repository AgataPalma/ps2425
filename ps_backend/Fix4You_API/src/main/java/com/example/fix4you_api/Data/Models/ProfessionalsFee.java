package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("ProfessionalsFees")
public class ProfessionalsFee {

    @Id
    private String id;

    @Field
    @NotNull(message = "Professional ID cannot be null")
    private String professionalId;

    @Field
    @NotNull(message = "Value cannot be null")
    @DecimalMin(value = "0.0", message = "Value must be a positive value")
    @Digits(integer = 3, fraction = 2, message = "Value must be a numeric value with up to 3 digits and 2 decimal places")
    private float value;

    @Field
    @NotNull(message = "Number of services cannot be null")
    @PositiveOrZero(message = "Number of services must be zero or a positive number")
    private int numberServices;

    @Field
    @NotBlank(message = "Related month cannot be blank")
    private String relatedMonth;

    @Field
    @NotNull(message = "Payment date cannot be null")
    @Future(message = "Payment date must be in the future")
    private LocalDateTime paymentDate;

    @Field
    @NotNull(message = "Payment status cannot be null")
    private PaymentStatusEnum paymentStatus;
}
