package com.example.fix4you_api.Data.Models;

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
@Document("Reviews")
public class Review {
    @Id
    private String id;

    @Field
    @NotNull(message = "Classification cannot be null")
    @DecimalMin(value = "0.0", message = "Classification must be a positive value")
    @Digits(integer = 1, fraction = 2, message = "Classification must be a numeric value with 1 digit and 2 decimal places")
    private float classification;

    @Field
    @NotBlank(message = "Review description cannot be blank")
    private String reviewDescription;

    @Field
    @NotNull(message = "Service ID cannot be null")
    private String serviceId;

    @Field
    @NotNull(message = "Reviewer ID cannot be null")
    private String reviewerId;              // user that makes a review (professional or a client)

    @Field
    @NotNull(message = "Professional ID cannot be null")
    private String reviewedId;              // user that receives a review (could be a professional or a client)

    @Field
    private LocalDateTime date;
}