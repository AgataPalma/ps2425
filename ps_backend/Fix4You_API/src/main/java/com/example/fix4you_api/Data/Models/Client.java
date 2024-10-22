package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.LanguageEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client extends User {

    @Field
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Field
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9\\-\\s()]*$", message = "Phone number must be valid and may contain only numbers, spaces, '-', '(', and ')'")
    private String phoneNumber;

    @Field
    @NotNull(message = "Languages cannot be null")
    private List<LanguageEnum> languages;

    @Field
    @NotNull(message = "Profile image cannot be null")
    @Size(max = 1048576, message = "Profile image must be less than 1 MB")
    private byte[] profileImage;

    @Field
    private boolean ageValidation;
}