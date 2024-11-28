package com.example.fix4you_api.Data.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalServiceCount {
    private String professionalId;
    private Long serviceCount;
}
