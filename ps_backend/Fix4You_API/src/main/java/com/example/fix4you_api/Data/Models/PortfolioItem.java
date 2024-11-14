package com.example.fix4you_api.Data.Models;

import jakarta.validation.constraints.NotBlank;
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
@Document("PortfolioItems")
public class PortfolioItem {

    @Id
    private String id;

    @Field
    @NotNull(message = "Professional ID cannot be null")
    private String professionalId;

    @Field
    private String filename;

    @Field
    private String contentType;

    @Field
    private byte[] fileData;

    @Field
    @NotBlank(message = "Description cannot be blank")
    private String description;
}
