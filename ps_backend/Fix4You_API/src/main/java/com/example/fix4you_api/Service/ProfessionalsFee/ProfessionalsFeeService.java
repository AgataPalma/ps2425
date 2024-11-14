package com.example.fix4you_api.Service.ProfessionalsFee;

import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.itextpdf.text.DocumentException;

import java.util.List;
import java.util.Map;

public interface ProfessionalsFeeService {
    List<ProfessionalsFee> getAllProfessionalsFee();
    ProfessionalsFee getProfessionalsFeeById(String id);
    List<ProfessionalsFee> getProfessionalsFeeForProfessionalId(String professionalId);
    ProfessionalsFee createProfessionalsFee(ProfessionalsFee professionalsFee);
    ProfessionalsFee createProfessionalFeeForRespectiveMonth(String professionalId, int numberServices, String relatedMonthYear);
    ProfessionalsFee updateProfessionalsFee(String id, ProfessionalsFee professionalsFee);
    ProfessionalsFee partialUpdateProfessionalsFee(String id, Map<String, Object> updates);
    void deleteProfessionalFee(String id);
    void deleteProfessionalFees(String professionalId);
    void checkAndCreateMonthlyFees();

    ProfessionalsFee setFeeAsPaid(String id) throws DocumentException;
}
