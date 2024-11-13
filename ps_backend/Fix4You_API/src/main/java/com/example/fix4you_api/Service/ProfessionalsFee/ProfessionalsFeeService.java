package com.example.fix4you_api.Service.ProfessionalsFee;

import com.example.fix4you_api.Data.Models.ProfessionalsFee;

public interface ProfessionalsFeeService {
    void checkAndCreateMonthlyFees();
    ProfessionalsFee createProfessionalsFee(String professionalId, int numberServices, String relatedMonthYear);
    void deleteProfessionalFees(String professionalId);
}
