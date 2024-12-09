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

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Professional extends Client {

    @Field
    @NotBlank(message = "A descrição não pode estar em branco")
    @Size(max = 500, message = "A descrição deve ter menos de 500 caracteres")
    private String description;

    @Field
    @NotBlank(message = "O NIF não pode estar em branco")
    @Pattern(regexp = "^[0-46-9]\\d{8}$", message = "O NIF deve conter exatamente 9 dígitos e não pode começar por 5")
    private String nif;

    @NotNull(message = "As línguas não podem ser nulas")
    private List<Language> languages;

    //@Field
    //@NotNull(message = "Locations range cannot be null")
    //private int locationsRange;

    @Field
    @NotNull(message = "Os pagamentos aceites não podem ser nulos")
    private List<PaymentMethod> acceptedPayments;

    @Field
    private int strikes;
}