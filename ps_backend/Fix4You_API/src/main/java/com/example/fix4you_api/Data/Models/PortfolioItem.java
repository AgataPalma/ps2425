package com.example.fix4you_api.Data.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("PortfolioItems")
public class PortfolioItem {
    @Id
    private String id;

    @Field
    @NotNull(message = "Professional ID não pode ser nulo")
    private String professionalId;

    @Field
    private byte[][] byteContent;

    @Field
    @NotBlank(message = "A descrição não pode estar em branco")
    private String description;
}