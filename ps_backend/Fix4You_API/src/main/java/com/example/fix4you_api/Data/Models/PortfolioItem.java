package com.example.fix4you_api.Data.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

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
    @NotNull(message = "File name cannot be null")
    private String filename;

    @Field
    @NotNull(message = "Content type cannot be null")
    private String contentType;

    @Field
    @NotNull(message = "File Data type cannot be null")
    private byte[] fileData;

    @Field
    @NotBlank(message = "Description cannot be blank")
    private String description;
}
