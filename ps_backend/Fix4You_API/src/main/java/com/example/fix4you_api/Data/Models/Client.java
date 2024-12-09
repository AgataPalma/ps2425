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
    @NotBlank(message = "O nome não pode estar em branco")
    @Size(max = 50, message = "O nome deve ter menos de 500 caracteres")
    private String name;

    @Field
    @NotBlank(message = "O número de telefone não pode estar em branco")
    @Pattern(regexp = "^\\+?[0-9\\-\\s()]*$", message = "O número de telefone deve ser válido e só pode conter números, espaços, '-', '(', and ')'")
    private String phoneNumber;

    @Field
    @NotBlank(message = "A localização não pode estar em branco")
    private String location;

    @Field
    private boolean ageValidation;

    @Field
    @NotNull(message = "A classificação não pode ser nula")
    private float rating;

    @Field
    @NotNull(message = "A imagem de perfil não pode ser nula")
    @Size(max = 1048576, message = "A imagem de perfil deve ter menos de 1 MB")
    private byte[] profileImage;

    @Field
    private boolean isSupended;

    @Field
    private String SuspensionReason;
}