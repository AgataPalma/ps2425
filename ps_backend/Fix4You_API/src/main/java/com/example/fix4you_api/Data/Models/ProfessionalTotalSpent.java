package com.example.fix4you_api.Data.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalTotalSpent {
    private String professionalId;
    private String professionalName;
    private Double totalSpent;
}
