package com.example.fix4you_api.Data.Models.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketRequestDTO {
    @NotBlank(message = "User ID não pode estar em branco")
    private String userId;

    @NotBlank(message = "Titulo não pode estar em branco")
    private String title;

    @NotBlank(message = "Descrição não pode estar em branco")
    private String description;
}