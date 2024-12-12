package com.example.fix4you_api.Service.ProfessionalsFee;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Models.ProfessionalTotalSpent;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.itextpdf.text.DocumentException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

public interface ProfessionalsFeeService {
    List<ProfessionalsFee> getAllProfessionalsFee();
    ProfessionalsFee getProfessionalsFeeById(String id);
    List<ProfessionalsFee> getProfessionalsFeeForProfessionalId(String professionalId);
    List<ProfessionalsFee> getProfessionalsFeeForProfessionalIdAndPaymentStatus(String professionalId, PaymentStatusEnum paymentStatusEnum);
    List<ProfessionalTotalSpent> getTopPriceProfessionals();
    List<ProfessionalTotalSpent> sendEmailTopPriceProfessionals();
    ProfessionalsFee createProfessionalsFee(ProfessionalsFee professionalsFee);
    ProfessionalsFee createProfessionalFeeForRespectiveMonth(String professionalId, int numberServices, String relatedMonthYear);
    ProfessionalsFee updateProfessionalsFee(String id, ProfessionalsFee professionalsFee);
    ProfessionalsFee partialUpdateProfessionalsFee(String id, Map<String, Object> updates);
    void deleteProfessionalFee(String id);
    void deleteProfessionalFeesForProfessional(String professionalId);
    void checkAndCreateMonthlyFees();
    ProfessionalsFee setFeeAsPaid(String id) throws DocumentException;
    SseEmitter streamSseEmitter(String professionalId);
    void sendSseMessageToProfessional(String professionalId, String message);
}
