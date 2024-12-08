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
    @NotNull(message = "A classificação não pode ser nula")
    @DecimalMin(value = "0.0", message = "A classificação deve ser um valor positivo")
    @Digits(integer = 1, fraction = 2, message = "A classificação deve ser um valor numérico com 1 dígito e 2 casas decimais")
    private float classification;

    @Field
    @NotBlank(message = "A descrição da revisão não pode estar em branco")
    private String reviewDescription;

    @Field
    @NotNull(message = "O ID do serviço não pode ser nulo")
    private String serviceId;

    @Field
    @NotNull(message = "O ID do revisor não pode ser nulo")
    private String reviewerId;              // user that makes a review (professional or a client)

    @Field
    @NotNull(message = "Professional ID não pode ser nulo")
    private String reviewedId;              // user that receives a review (could be a professional or a client)

    @Field
    private LocalDateTime date;
}