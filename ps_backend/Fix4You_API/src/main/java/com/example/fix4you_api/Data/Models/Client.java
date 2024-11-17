package com.example.fix4you_api.Data.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client extends User {

    @Field
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Description must be less than 500 characters")
    private String name;

    @Field
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9\\-\\s()]*$", message = "Phone number must be valid and may contain only numbers, spaces, '-', '(', and ')'")
    private String phoneNumber;

    @Field
    @NotBlank(message = "Location cannot be blank")
    private String location;

    @Field
    private boolean ageValidation;

    @Field
    @NotNull(message = "Rating cannot be null")
    private float rating;

    @Field
    private Image image;

}