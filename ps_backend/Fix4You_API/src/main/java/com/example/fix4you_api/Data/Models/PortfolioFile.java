package com.example.fix4you_api.Data.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioFile {
    @Field
    private String filename;

    @Field
    private String contentType;

    @Field
    private String base64Encoder;
}
