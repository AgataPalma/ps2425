package com.example.fix4you_api.Data.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketRequest {
    @NotNull(message = "Ticket cannot be null")
    private Ticket ticket;

    @NotBlank(message = "Description cannot be blank")
    private String description;
}