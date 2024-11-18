package com.example.fix4you_api.Data.Models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionalRegistrationRequest {
    @NotNull(message = "Professional data cannot be null")
    @Valid
    private Professional professional;
    @NotNull(message = "Category descriptions cannot be null")
    @Valid
    private List<CategoryDescription> categoryDescriptions;
}
